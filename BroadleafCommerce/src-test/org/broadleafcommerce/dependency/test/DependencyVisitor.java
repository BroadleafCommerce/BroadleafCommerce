/***
 * ASM examples: examples showing how ASM can be used
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.broadleafcommerce.dependency.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.CodeVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * DependencyVisitor - the ASM example is re-written to be compatible with ASM 1.5.3
 * 
 * @author jfischer
 */
public class DependencyVisitor implements ClassVisitor, CodeVisitor {

    private Set<String> packages = new HashSet<String>();
    private Map<String, Map<String, Integer>> groups = new HashMap<String, Map<String, Integer>>();
    private Map<String, Integer> current;

    public Map<String, Map<String, Integer>> getGlobals() {
        return groups;
    }

    public Set<String> getPackages() {
        return packages;
    }

    // ClassVisitor

    public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
        String p = getGroupKey(name);
        current = groups.get(p);
        if (current == null) {
            current = new HashMap<String, Integer>();
            groups.put(p, current);
        }

        addName(superName);
        addNames(interfaces);
    }

    public void visitAttribute(Attribute attr) {
    }

    public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
        addDesc(desc);
        if (value instanceof Type)
            addType((Type) value);
    }

    public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
        addMethodDesc(desc);
        addNames(exceptions);
        return this;
    }

    public void visitSource(String source, String debug) {
    }

    public void visitInnerClass(
            String name,
            String outerName,
            String innerName,
            int access)
    {
    }

    public void visitOuterClass(String owner, String name, String desc) {
    }

    // MethodVisitor

    public void visitTypeInsn(int opcode, String desc) {
        if (desc.charAt(0) == '[')
            addDesc(desc);
        else
            addName(desc);
    }

    public void visitFieldInsn(
            int opcode,
            String owner,
            String name,
            String desc)
    {
        addName(owner);
        addDesc(desc);
    }

    public void visitMethodInsn(
            int opcode,
            String owner,
            String name,
            String desc)
    {
        addName(owner);
        addMethodDesc(desc);
    }

    public void visitLdcInsn(Object cst) {
        if (cst instanceof Type)
            addType((Type) cst);
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        addDesc(desc);
    }

    public void visitLocalVariable(String name, String desc, Label start, Label end, int index) {
    }

    public void visitCode() {
    }

    public void visitInsn(int opcode) {
    }

    public void visitIntInsn(int opcode, int operand) {
    }

    public void visitVarInsn(int opcode, int var) {
    }

    public void visitJumpInsn(int opcode, Label label) {
    }

    public void visitLabel(Label label) {
    }

    public void visitIincInsn(int var, int increment) {
    }

    public void visitTableSwitchInsn(
            int min,
            int max,
            Label dflt,
            Label[] labels)
    {
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    }

    public void visitTryCatchBlock(
            Label start,
            Label end,
            Label handler,
            String type)
    {
        addName(type);
    }

    public void visitLineNumber(int line, Label start) {
    }

    public void visitMaxs(int maxStack, int maxLocals) {
    }

    // common

    public void visitEnd() {
    }

    // ---------------------------------------------

    private String getGroupKey(String name) {
        int n = name.lastIndexOf('/');
        if (n > -1)
            name = name.substring(0, n);
        packages.add(name);
        return name;
    }

    private void addName(String name) {
        if (name == null)
            return;
        String p = getGroupKey(name);
        if (current.containsKey(p)) {
            current.put(p, current.get(p) + 1);
        } else {
            current.put(p, 1);
        }
    }

    private void addNames(String[] names) {
        for (int i = 0; names != null && i < names.length; i++)
            addName(names[i]);
    }

    private void addDesc(String desc) {
        addType(Type.getType(desc));
    }

    private void addMethodDesc(String desc) {
        addType(Type.getReturnType(desc));
        Type[] types = Type.getArgumentTypes(desc);
        for (int i = 0; i < types.length; i++)
            addType(types[i]);
    }

    private void addType(Type t) {
        switch (t.getSort()) {
        case Type.ARRAY:
            addType(t.getElementType());
            break;
        case Type.OBJECT:
            addName(t.getClassName().replace('.', '/'));
            break;
        }
    }
}
