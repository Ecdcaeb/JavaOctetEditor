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

package cn.enaium.joe.gui.panel;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.annotation.Indeterminate;
import cn.enaium.joe.gui.panel.popup.TaskListPopup;
import cn.enaium.joe.util.task.AbstractTask;
import cn.enaium.joe.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Enaium
 */
public class BottomPanel extends JPanel {

    public BottomPanel() {
        super(new GridLayout(1, 2));
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(new JLabel("\u00A9 JavaOctetEditor 2024"));
        TaskListPopup taskListPopup = new TaskListPopup();
        JProgressBar jProgressBar = new JProgressBar() {{

            JProgressBar self = this;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point locationOnScreen = self.getLocationOnScreen();
                    taskListPopup.place(locationOnScreen.x, locationOnScreen.y - taskListPopup.getPreferredSize().height);
                }
            });
        }};

        add(jProgressBar);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            List<Pair<AbstractTask<?>, CompletableFuture<?>>> task = JavaOctetEditor.getInstance().TASKS.getTask();
            if (task.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    jProgressBar.setValue(0);
                    jProgressBar.setStringPainted(true);
                    jProgressBar.setIndeterminate(false);
                    jProgressBar.setString("");
                    jProgressBar.repaint();
                });
            } else {
                Pair<AbstractTask<?>, CompletableFuture<?>> classCompletableFuturePair = task.get(task.size() - 1);
                SwingUtilities.invokeLater(() -> {
                    int progress = classCompletableFuturePair.getKey().getProgress();
                    if (!classCompletableFuturePair.getKey().getClass().isAnnotationPresent(Indeterminate.class)) {
                        jProgressBar.setValue(progress);
                        jProgressBar.setStringPainted(true);
                        jProgressBar.setIndeterminate(false);
                        jProgressBar.setString(String.format("%s:%s", classCompletableFuturePair.getKey().getName(), progress) + "%");
                    } else {
                        jProgressBar.setString(classCompletableFuturePair.getKey().getName());
                        jProgressBar.setIndeterminate(true);
                    }
                    jProgressBar.repaint();
                });
            }
        }, 1, 1, TimeUnit.MILLISECONDS);
    }
}
