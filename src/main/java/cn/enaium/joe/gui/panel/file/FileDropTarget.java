/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.gui.panel.file;

import org.pmw.tinylog.Logger;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Enaium
 */
public class FileDropTarget implements DropTargetListener {

    private boolean drop = false;
    private final List<File> files = new ArrayList<>();

    private final Predicate<File> predicate;
    private final Consumer<List<File>> consumer;

    public FileDropTarget(Predicate<File> predicate, Consumer<List<File>> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        drop = false;
        files.clear();
        Transferable t = dtde.getTransferable();
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
                if (td instanceof List) {
                    drop = true;
                    for (Object value : ((List) td)) {
                        if (value instanceof File) {
                            File file = (File) value;
                            if (!predicate.test(file)) {
                                drop = false;
                                break;
                            } else {
                                files.add(file);
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                Logger.info(ex);
            }
        }
        if (drop) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        drop = false;
        files.clear();
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        consumer.accept(files);
        drop = false;
        files.clear();
    }
}
