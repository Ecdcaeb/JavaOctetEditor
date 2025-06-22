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

package cn.enaium.joe;

import cn.enaium.joe.util.config.extend.CFRConfig;
import cn.enaium.joe.util.config.extend.ProcyonConfig;
import cn.enaium.joe.util.config.extend.VineFlowerConfig;
import cn.enaium.joe.util.config.ConfigManager;
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.util.event.EventManager;
import cn.enaium.joe.gui.panel.BorderPanel;
import cn.enaium.joe.gui.panel.BottomPanel;
import cn.enaium.joe.gui.panel.file.tree.CenterPanel;
import cn.enaium.joe.gui.panel.file.tabbed.FileTabbedPanel;
import cn.enaium.joe.gui.component.FileTree;
import cn.enaium.joe.gui.panel.menu.*;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.task.TaskManager;
import cn.enaium.joe.util.*;
import cn.enaium.joe.util.reflection.ReflectionHelper;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author Enaium
 */
public class JavaOctetEditor {
    private static JavaOctetEditor INSTANCE;

    public static final String TITLE = "JavaOctetEditor";

    public static final String VERSION = "1.4.6";

    public JFrame window;

    private Jar jar;

    public FileTabbedPanel fileTabbedPanel;

    public FileTree fileTree;

    public BottomPanel bottomPanel;

    public EventManager EVENTS;

    public final ConfigManager CONFIG;

    public TaskManager TASKS;


    public JavaOctetEditor() {
        INSTANCE = this;
        
        EVENTS = new EventManager();
        CONFIG = new ConfigManager();
        CONFIG.addByInstance(new ApplicationConfig());
        CONFIG.addByInstance(new CFRConfig());
        CONFIG.addByInstance(new VineFlowerConfig());
        CONFIG.addByInstance(new ProcyonConfig());
        CONFIG.load();
        TASKS = new TaskManager();
        Runtime.getRuntime().addShutdownHook(new Thread(CONFIG::save));

        Integer value = CONFIG.getByClass(ApplicationConfig.class).scale.getValue();

        if (value > 0) {
            System.setProperty("sun.java2d.uiScale", value.toString());
        }

        FlatDarkLaf.setup();
        UIManager.put("Tree.paintLines", true);

        fileTabbedPanel = new FileTabbedPanel();
        fileTree = new FileTree();
        bottomPanel = new BottomPanel();
    }

    public void run() {

        ToolTipManager.sharedInstance().setInitialDelay(0);
        FlatDarkLaf.setup();
        AbstractTokenMakerFactory abstractTokenMakerFactory = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        abstractTokenMakerFactory.putMapping("text/custom", BytecodeTokenMaker.class.getName());
        window = new JFrame(TITLE);
        window.setIconImage(new FlatSVGIcon("icons/logo.svg").getImage());

        window.setJMenuBar(new JMenuBar() {{
            add(new FileMenu());
            add(new SearchMenu());

            AttachMenu attachMenu = new AttachMenu() {{
                if (!ReflectionHelper.isClassExist("com.sun.tools.attach.VirtualMachine")) {
                    setEnabled(false);
                }
            }};
            add(attachMenu);
            add(new MappingMenu());
            add(new ConfigMenu());
            add(new HelpMenu());
        }});

        window.setContentPane(new BorderPanel() {{
            setCenter(new CenterPanel());
            setBottom(bottomPanel);
        }});


        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MessageUtil.confirm(LangUtil.i18n("dialog.wantCloseWindow"), LangUtil.i18n("warning"), () -> {
                    window.dispose();
                    System.exit(0);
                }, () -> {
                });
            }
        });
        window.setSize(Util.screenSize(1000, 600));
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public Jar getJar() {
        return jar;
    }

    public void setJar(Jar jar) {
        this.jar = jar;
        fileTree.refresh(jar);
    }

    public void refreshJar() {
        jar.refresh();
        fileTree.refresh(getJar());
    }

    public static JavaOctetEditor getInstance() {
        return INSTANCE;
    }
}
