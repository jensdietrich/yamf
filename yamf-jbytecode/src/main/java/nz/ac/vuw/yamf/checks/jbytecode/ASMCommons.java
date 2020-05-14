package nz.ac.vuw.yamf.checks.jbytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ASMCommons {

    static int ASM_VERSION = Opcodes.ASM7;

    static void analyse (File file, ClassVisitor visitor) throws IOException {
        String name = file.getName();
        if (file.isDirectory()) {
            for (File child:file.listFiles()) {
                analyse(child,visitor);
            }
        }

        else if (name.endsWith(".class")) {
            InputStream in = new FileInputStream(file);
            // debug("analysing " + file.getAbsolutePath());
            analyse(in,visitor);
            in.close();
        }

        else if (name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".war") || name.endsWith(".ear")) {
            ZipFile zip = new ZipFile(file);
            analyse(zip,visitor);
        }
    }

    static void analyse (ZipFile zip, ClassVisitor visitor) throws IOException {
        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".class")) {
                InputStream in = zip.getInputStream(e);
                // debug("analysing " + zip.getName() + " # " + e.getName());
                analyse(in,visitor);
                in.close();
            }
        }
    }

    static void analyse (InputStream in, ClassVisitor visitor) throws IOException {
        new ClassReader(in).accept(visitor, 0);
    }

    static boolean checkFlag (int flags, int opCode) {
        return (flags & opCode) == opCode;
    }

    static String toJava (String name) {
        return name;
    }
}
