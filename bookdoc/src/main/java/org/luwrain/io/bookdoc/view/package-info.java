// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

/**
 * Provides the rendering and layout engine for bookdoc documents.
 *
 * <p>The rendering pipeline takes a {@link org.luwrain.io.bookdoc.Doc document}
 * and a target width, then produces a two-dimensional grid of
 * {@link org.luwrain.io.bookdoc.view.Row rows} with absolute (x, y) positions.
 * The result can be accessed through an {@link org.luwrain.io.bookdoc.view.Iterator}
 * for screen-reader-friendly row-by-row navigation, or through a
 * {@link org.luwrain.io.bookdoc.view.Layout} for visual line composition.</p>
 *
 * <h2>Rendering pipeline</h2>
 * <ol>
 *   <li>{@link org.luwrain.io.bookdoc.view.NodeGeom NodeGeom} calculates
 *       widths, heights, and positions for all nodes in the document tree</li>
 *   <li>{@link org.luwrain.io.bookdoc.view.DefaultRowPartsBuilder DefaultRowPartsBuilder}
 *       traverses the tree and splits paragraph runs into
 *       {@link org.luwrain.io.bookdoc.view.RowPart RowPart} objects using
 *       {@link org.luwrain.io.bookdoc.view.RowPartsSplitter RowPartsSplitter}</li>
 *   <li>{@link org.luwrain.io.bookdoc.view.View View} assembles row parts into
 *       {@link org.luwrain.io.bookdoc.view.Row Row} objects and assigns
 *       absolute coordinates</li>
 *   <li>{@link org.luwrain.io.bookdoc.view.Layout Layout} groups rows into
 *       visual lines for display</li>
 * </ol>
 *
 * <h2>Text extraction</h2>
 * <p>The package also provides text extraction utilities:</p>
 * <ul>
 *   <li>{@link org.luwrain.io.bookdoc.view.TextExtractorWhole TextExtractorWhole}
 *       extracts the full text of a document subtree</li>
 *   <li>{@link org.luwrain.io.bookdoc.view.TextExtractorFragment TextExtractorFragment}
 *       extracts a bounded fragment between two positions</li>
 * </ul>
 *
 * @see org.luwrain.io.bookdoc.view.View
 * @see org.luwrain.io.bookdoc.view.Iterator
 * @see org.luwrain.io.bookdoc.view.Layout
 */
package org.luwrain.io.bookdoc.view;
