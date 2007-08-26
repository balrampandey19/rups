/*
 * $Id: PdfDocument.java 2884 2007-08-15 09:28:41Z blowagie $
 * Copyright (c) 2007 Bruno Lowagie
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lowagie.rups.nodetypes;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.lowagie.rups.interfaces.TreeNodeWithIcon;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;

/**
 * A node in the OutlineTree.
 */
public class OutlineTreeNode extends DefaultMutableTreeNode
	implements TreeNodeWithIcon {

	/** A serial version uid */
	private static final long serialVersionUID = 5437651809665762952L;

	/** The corresponding tree node in the PdfTree. */
	protected PdfObjectTreeNode object_node;
	
	/** Creates the root node for the OutlineTree. */
	public OutlineTreeNode() {
		super("Bookmarks");
	}

	/**
	 * Creates a node corresponding with a node in the PdfTree.
	 * @param	node	a corresponding node
	 */
	public OutlineTreeNode(PdfObjectTreeNode node) {
		super();
		this.object_node = node;
		PdfDictionary dict = (PdfDictionary)node.getPdfObject();
		this.setUserObject(dict.get(PdfName.TITLE));
	}

    /**
     * Gets the node in the PdfTree that corresponds with this
     * OutlineTreeNode.
     * @return	a PdfObjectTreeNode in the PdfTree
     */
	public PdfObjectTreeNode getCorrespondingPdfObjectNode() {
		return object_node;
	}

    /**
     * Getter for the icon that is to be used for this tree node.
     * @return	the icon corresponding with the object
     */
    public Icon getIcon() {
		return new ImageIcon(OutlineTreeNode.class
			.getResource("icons/outline.png"));
    }
}