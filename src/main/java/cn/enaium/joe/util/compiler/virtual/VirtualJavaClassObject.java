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

package cn.enaium.joe.util.compiler.virtual;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Enaium
 * @since 1.4.0
 */
public class VirtualJavaClassObject extends SimpleJavaFileObject {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final byte[] content;

    public VirtualJavaClassObject(String className, byte[] content) {
        super(URI.create("string:///" + className.replace(".", "/") + Kind.CLASS.extension), Kind.CLASS);
        this.content = content;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(content);
    }
}
