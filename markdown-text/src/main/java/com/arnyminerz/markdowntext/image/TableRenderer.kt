package com.arnyminerz.markdowntext.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Helps on creating [Bitmap]s from table contents.
 *
 * **No error handling is done. Check that all the sizes match. All the rows and header must have
 * the same number of columns!**
 * @param headers A list of the headers of the table.
 * @param rows A matrix with the contents of each row of the table.
 */
class TableRenderer(
    private val headers: List<String>,
    private val rows: List<List<String>>,
) {
    companion object {
        private const val HorizontalMargin = 15f
        private const val VerticalMargin = 10f

        private val TablePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        private val HeaderBackground = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL
        }
        private val HeaderText = Paint().apply {
            typeface = Typeface.DEFAULT_BOLD
            textSize = 20f
            color = Color.BLACK
        }

        private val RowBackground = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        private val RowText = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 18f
            color = Color.BLACK
        }
    }

    /**
     * Draws a row of the table.
     * @param values The values for each column of the row.
     * @param columnWidths The width of each column.
     * @param rowHeight The height of the row.
     * @param textPaint The paint to use for the texts.
     * @param backgroundPaint The paint to use for painting backgrounds of the row.
     * @param verticalOffset The current vertical offset in relation with the canvas' vertical space.
     * @param linesPaint The paint to use for drawing lines.
     * @param textOffset The offset to give to the text in relation with the border of the table.
     */
    private fun Canvas.drawRow(
        values: List<String>,
        columnWidths: List<Int>,
        rowHeight: Int,
        textPaint: Paint,
        backgroundPaint: Paint,
        verticalOffset: Int,
        linesPaint: Paint = TablePaint,
        textOffset: Float = 5f,
    ): Int {
        // Convert the horizontal margin to int
        val iHMargin = HorizontalMargin.toInt()
        val iVMargin = VerticalMargin.toInt()
        val rowRect = Rect(
            // X1: The horizontal margin as integer
            iHMargin,
            // Y1: The current vertical offset, plus the margin to the top
            verticalOffset + iVMargin,
            // X2: The width of the canvas, minus the horizontal margin
            width - iHMargin,
            // Y2: The height of the row given, plus twice the horizontal margin
            verticalOffset + iVMargin + rowHeight + iVMargin,
        )

        // Draw the row's rectangle background
        drawRect(rowRect, backgroundPaint)
        // Draw the row's rectangle stroke
        drawRect(rowRect, linesPaint)

        var offset = HorizontalMargin
        for ((index, value) in values.withIndex()) {
            // Add initial margin
            offset += HorizontalMargin

            // Get the size of the label
            val textWidth = columnWidths[index]

            // Draw the label
            drawText(
                value,
                offset,
                // The vertical position is equal to the current vertical offset, plus the vertical
                // margin, plus the offset given for the texts, plus the height of the row, since
                // the text starts to draw from the bottom
                verticalOffset + VerticalMargin + textOffset + rowHeight,
                textPaint,
            )

            // Add the text width plus the end margin
            offset += textWidth + HorizontalMargin

            // Draw the horizontal divider
            drawLine(
                offset,
                verticalOffset + VerticalMargin,
                offset,
                verticalOffset + 2 * VerticalMargin + rowHeight,
                linesPaint,
            )
        }

        return verticalOffset + rowRect.height()
    }

    private fun List<String>.computeBounds(paint: Paint) = map { value ->
        Rect().apply { paint.getTextBounds(value, 0, value.length, this) }
    }

    /**
     * Draws the table as a Bitmap.
     * @return A Bitmap with the drawn table.
     */
    private fun draw(): Bitmap {
        // First we want to get the maximum width of each column, in any row
        // Measure the bounds of all the headers
        val headersBounds = headers.computeBounds(HeaderText)
        // Get a list of the bounds of each row
        val rowsBounds = rows.map { it.computeBounds(RowText) }
        // Compute a list with each row of the table, including headers
        val perRowColumnBounds = listOf(headersBounds, *rowsBounds.toTypedArray())
        // Get the number of columns there are (headers override rows)
        val colsCount = headers.size

        // Get the width of each column
        val columnWidths = (0 until colsCount)
            // Max the index of each column
            .map { column ->
                // And get the maximum width of each row for the given column
                perRowColumnBounds.maxOf { it[column].width() }
            }
        // Get the height of the tallest row
        val rowHeight = perRowColumnBounds.maxOf { sizes -> sizes.maxOf { it.height() } }

        val hMargin = HorizontalMargin.toInt()
        val vMargin = VerticalMargin.toInt()

        // Get a sum of the widths of all the columns, with horizontal margins
        val headersWidth = headersBounds.sumOf { hMargin + it.width() + hMargin }

        val bitmap = Bitmap.createBitmap(
            hMargin + headersWidth + hMargin,
            vMargin * 2 + (vMargin * 2 + rowHeight) * (rows.size + 1),
            Bitmap.Config.ARGB_8888,
        )
        with(Canvas(bitmap)) {
            var verticalOffset = 0
            verticalOffset = drawRow(
                headers,
                columnWidths,
                rowHeight,
                HeaderText,
                HeaderBackground,
                verticalOffset,
            )

            for (row in rows)
                verticalOffset = drawRow(
                    row,
                    columnWidths,
                    rowHeight,
                    RowText,
                    RowBackground,
                    verticalOffset,
                )
        }
        return bitmap
    }

    /**
     * Draws the table as an [ImageBitmap].
     */
    val imageBitmap: ImageBitmap
        get() = draw().asImageBitmap()
}
