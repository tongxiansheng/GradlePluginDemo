package com.demo.myplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.demo.myplugin.entity.TransformEntity
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

class ClassModifier {

    private static List<ClassPath> sAppendedClassPath = new ArrayList<>()
    private static List<CtClass> sModifiedClass = new ArrayList<>()

    static void init(AppExtension android) {
        if (android == null)
            return
        List<File> classPath = android.getBootClasspath()
        if (null != classPath && !classPath.isEmpty()) {
            for (File path : classPath) {
                appendClassPath(path.getAbsolutePath())
            }
        }
    }

    static void initLib(LibraryExtension android) {
        if (android == null)
            return
        List<File> classPath = android.getBootClasspath()
        if (null != classPath && !classPath.isEmpty()) {
            for (File path : classPath) {
                appendClassPath(path.getAbsolutePath())
            }
        }
    }

    static void appendClassPath(String path) {
        println("ClassModifier: appendClassPath: ${path}")
        ClassPath appended = ClassPool.getDefault().appendClassPath(path)
        sAppendedClassPath.add(appended)
    }

    static void workDoneClean() {
        if (null != sModifiedClass) {
            for (CtClass c : sModifiedClass) {
                if (null != c) {
                    try {
                        c.detach()
                    } catch (Exception ignore) {
                        ignore.printStackTrace()
                    }
                }
            }
            sModifiedClass.clear()
        }
        if (null != sAppendedClassPath) {
            for (ClassPath p : sAppendedClassPath) {
                if (null != p) {
                    try {
                        ClassPool.getDefault().removeClassPath(p)
                    } catch (Exception ignore) {
                        ignore.printStackTrace()
                    }
                }
            }
            sAppendedClassPath.clear()
        }
    }

    static void writeLog(TransformEntity entity) {
        for (String activity : ValueHolder.activities) {
            println("inject $activity")
            CtClass clazz

            try {
                clazz = ClassPool.getDefault().get(activity)
                if (clazz.isFrozen()) {
                    clazz.defrost()
                }
//                clazz.getClassFile().setMajorVersion(javassist.bytecode.ClassFile.JAVA_7)
                CtMethod init = clazz.getDeclaredMethod("onCreate")
                init.insertBefore("android.util.Log.d(\"inject\", \"${activity}\");")
                String path = ClassPool.getDefault().find(activity).getFile().replace("/${activity.replace(".", "/")}.class", "")
                println("file path ori ${path}")
                clazz.writeFile(path)
            } catch (Exception e) {
                e.printStackTrace()
            } finally {
                if (null != clazz) {
                    sModifiedClass.add(clazz)
                }
            }
        }
    }
}
