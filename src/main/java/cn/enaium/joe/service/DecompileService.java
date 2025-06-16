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

package cn.enaium.joe.service;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.service.decompiler.IDecompiler;
import cn.enaium.joe.service.decompiler.VineFlowerDecompiler;
import cn.enaium.joe.service.decompiler.CFRDecompiler;
import cn.enaium.joe.service.decompiler.ProcyonDecompiler;

import java.util.function.Supplier;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class DecompileService {
    public enum Service{
        VineFlower(VineFlowerDecompiler::new),
        CFR(CFRDecompiler::new),
        Procyon(ProcyonDecompiler::new);


        private final Supplier<IDecompiler> decompilerSupplier;
        Service(Supplier<IDecompiler> supplier){
            this.decompilerSupplier = supplier;
        }

        public IDecompiler getDecompiler(){
            return decompilerSupplier.get();
        }
    }


    public static IDecompiler getService() {
        return JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).decompilerMode.getValue().getDecompiler();
    }
}
