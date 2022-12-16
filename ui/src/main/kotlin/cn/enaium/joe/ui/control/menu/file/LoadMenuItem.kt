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

package cn.enaium.joe.ui.control.menu.file

import cn.enaium.joe.core.config.extend.ApplicationConfig

import cn.enaium.joe.core.task.InputJarTask
import cn.enaium.joe.ui.JavaOctetEditor
import cn.enaium.joe.ui.JavaOctetEditor.Companion.config
import cn.enaium.joe.ui.JavaOctetEditor.Companion.event
import cn.enaium.joe.ui.JavaOctetEditor.Companion.stage
import cn.enaium.joe.ui.JavaOctetEditor.Companion.task
import cn.enaium.joe.ui.event.LoadJar
import cn.enaium.joe.ui.util.i18n
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import java.io.File

/**
 * @author Enaium
 * @since 2.0.0
 */
class LoadMenuItem : MenuItem(i18n("menu.file.load")) {
    init {
        setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Resource File"
            fileChooser.initialDirectory = File(System.getProperty("user.home"))
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Zip File(*.zip,*.jar)", "*.jar", "*.zip")
            )
            val showOpenDialog = fileChooser.showOpenDialog(stage)
            if (showOpenDialog != null) {
                config.getByClass(ApplicationConfig::class.java).loadRecent.value.add(showOpenDialog.absolutePath)
                task.submit(InputJarTask(showOpenDialog.absoluteFile)).thenAccept {
                    event.call(LoadJar(it))
                }
            }
        }
    }
}