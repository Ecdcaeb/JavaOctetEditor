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

import cn.enaium.joe.util.KeyStrokeUtil;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.StringUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author Enaium
 */
public class CodeAreaPanel extends BorderPanel implements ActionListener {

    private final RSyntaxTextArea textArea;
    private final JTextField searchField;
    private final JCheckBox regexCB;
    private final JCheckBox matchCaseCB;

    public CodeAreaPanel() {
        textArea = new RSyntaxTextArea();
        textArea.setCodeFoldingEnabled(true);
        Theme theme;
        try {
            theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        theme.apply(textArea);

        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(30);
        toolBar.add(searchField);
        final JButton nextButton = new JButton(LangUtil.i18n("button.findNext"));
        nextButton.setActionCommand("FindNext");
        nextButton.addActionListener(this);
        toolBar.add(nextButton);
        searchField.addActionListener(e -> nextButton.doClick(0));
        JButton prevButton = new JButton(LangUtil.i18n("button.findPrevious"));
        prevButton.setActionCommand("FindPrev");
        prevButton.addActionListener(this);
        toolBar.add(prevButton);
        regexCB = new JCheckBox("Regex");
        toolBar.add(regexCB);
        matchCaseCB = new JCheckBox("Match Case");
        toolBar.add(matchCaseCB);
        toolBar.setVisible(false);
        setTop(toolBar);
        setCenter(new RTextScrollPane(textArea) {{
            KeyStrokeUtil.register(textArea, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), () -> {
                if (!StringUtil.isBlank(textArea.getSelectedText())) {
                    searchField.setText(textArea.getSelectedText());
                    searchField.setFocusable(true);
                }
                toolBar.setVisible(true);
                searchField.requestFocus();
            });
            KeyStrokeUtil.register(textArea, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), () -> {
                toolBar.setVisible(false);
            });
        }});
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // "FindNext" => search forward, "FindPrev" => search backward
        String command = e.getActionCommand();
        boolean forward = "FindNext".equals(command);

        // Create an object defining our search parameters.
        SearchContext context = new SearchContext();
        String text = searchField.getText();
        if (text.length() == 0) {
            return;
        }
        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setSearchForward(forward);
        context.setWholeWord(false);

        SearchEngine.find(textArea, context);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }
}
