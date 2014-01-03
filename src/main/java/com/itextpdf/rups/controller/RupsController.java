/*
 * $Id$
 *
 * Copyright 2007 Bruno Lowagie.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.itextpdf.rups.controller;

import com.itextpdf.rups.io.FileChooserAction;
import com.itextpdf.rups.io.FileCloseAction;
import com.itextpdf.rups.model.PdfFile;
import com.itextpdf.rups.view.Console;
import com.itextpdf.rups.view.PageSelectionListener;
import com.itextpdf.rups.view.RupsMenuBar;
import com.itextpdf.rups.view.contextmenu.ConsoleContextMenu;
import com.itextpdf.rups.view.contextmenu.ContextMenuMouseListener;
import com.itextpdf.rups.view.itext.treenodes.PdfObjectTreeNode;
import com.itextpdf.rups.view.itext.treenodes.PdfTrailerTreeNode;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStamper;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;

/**
 * This class controls all the GUI components that are shown in
 * the RUPS application: the menu bar, the panels,...
 */
public class RupsController extends Observable
	implements TreeSelectionListener, PageSelectionListener {

	// member variables

	/* file and controller */
	/** The Pdf file that is currently open in the application. */
	protected PdfFile pdfFile;
	/**
	 * Object with the GUI components for iText.
	 * @since	iText 5.0.0 (renamed from reader which was confusing because reader is normally used for a PdfReader instance)
	 */
	protected PdfReaderController readerController;

	/* main components */
	/** The JMenuBar for the RUPS application. */
	protected RupsMenuBar menuBar;
	/** Contains all other components: the page panel, the outline tree, etc. */
	protected JSplitPane masterComponent;


	// constructor
	/**
	 * Constructs the GUI components of the RUPS application.
	 */
	public RupsController(Dimension dimension) {
		// creating components and controllers
        menuBar = new RupsMenuBar(this);
        addObserver(menuBar);
		Console console = Console.getInstance();
		addObserver(console);
		readerController = new PdfReaderController(this, this);
		addObserver(readerController);

        // creating the master component
		masterComponent = new JSplitPane();
		masterComponent.setOrientation(JSplitPane.VERTICAL_SPLIT);
		masterComponent.setDividerLocation((int)(dimension.getHeight() * .70));
		masterComponent.setDividerSize(2);

		JSplitPane content = new JSplitPane();
		masterComponent.add(content, JSplitPane.TOP);
		JSplitPane info = new JSplitPane();
		masterComponent.add(info, JSplitPane.BOTTOM);

		content.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		content.setDividerLocation((int)(dimension.getWidth() * .6));
		content.setDividerSize(1);
        content.add(new JScrollPane(readerController.getPdfTree()), JSplitPane.LEFT);
		content.add(readerController.getNavigationTabs(), JSplitPane.RIGHT);

		info.setDividerLocation((int) (dimension.getWidth() * .3));
		info.setDividerSize(1);
		info.add(readerController.getObjectPanel(), JSplitPane.LEFT);
		JTabbedPane editorPane = readerController.getEditorTabs();
		JScrollPane cons = new JScrollPane(console.getTextArea());
        console.getTextArea().addMouseListener(new ContextMenuMouseListener(ConsoleContextMenu.getPopupMenu(console.getTextArea()), cons));
		editorPane.addTab("Console", null, cons, "Console window (System.out/System.err)");
		editorPane.setSelectedComponent(cons);
		info.add(editorPane, JSplitPane.RIGHT);

	}

	/**
	 *
	 */
	public RupsController(Dimension dimension, File f) {
		this(dimension);
		loadFile(f);
	}
	/** Getter for the menubar. */
	public RupsMenuBar getMenuBar() {
		return menuBar;
	}

	/** Getter for the master component. */
	public Component getMasterComponent() {
		return masterComponent;
	}

	// Observable

	/**
	 * @see java.util.Observable#notifyObservers(java.lang.Object)
	 */
	@Override
	public void notifyObservers(Object obj) {
		if (obj instanceof FileChooserAction) {
			File file = ((FileChooserAction)obj).getFile();

            /* save check */
            if ( ((FileChooserAction)obj).isNewFile() ) {
                saveFile(file);
            } else {
                loadFile(file);
            }
			return;
		}
		if (obj instanceof FileCloseAction) {
			pdfFile = null;
			setChanged();
			super.notifyObservers(RupsMenuBar.CLOSE);
			return;
		}
	}

	/**
	 * @param file the file to load
	 */
	public void loadFile(File file) {
		try {
			pdfFile = new PdfFile(file);
			setChanged();
			super.notifyObservers(RupsMenuBar.OPEN);
			readerController.startObjectLoader(pdfFile);
		}
		catch(IOException ioe) {
			JOptionPane.showMessageDialog(masterComponent, ioe.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
		}
		catch (DocumentException de) {
			JOptionPane.showMessageDialog(masterComponent, de.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
		}
	}

    /**
     * Saves the pdf to the disk
     * @param file java.io.File file to save
     */
    public void saveFile(File file) {
        try {
            if ( !file.getName().endsWith(".pdf") ) {
                file = new File(file.getPath() + ".pdf");
            }
            PdfStamper stamper = new PdfStamper(pdfFile.getPdfReader(), new FileOutputStream(file));
            stamper.close();
            JOptionPane.showMessageDialog(masterComponent, "File saved.", "Dialog", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException de) {
            JOptionPane.showMessageDialog(masterComponent, de.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(masterComponent, ioe.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
        }
    }

	// tree selection

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent evt) {
		Object selectednode = readerController.getPdfTree().getLastSelectedPathComponent();
		if (selectednode instanceof PdfTrailerTreeNode) {
			menuBar.update(this, RupsMenuBar.FILE_MENU);
			return;
		}
		if (selectednode instanceof PdfObjectTreeNode) {
			readerController.update(this, selectednode);
		}
	}

	// page navigation

	/**
	 * @see com.itextpdf.rups.view.PageSelectionListener#gotoPage(int)
	 */
	public int gotoPage(int pageNumber) {
		readerController.gotoPage(pageNumber);
		return pageNumber;
	}
}
