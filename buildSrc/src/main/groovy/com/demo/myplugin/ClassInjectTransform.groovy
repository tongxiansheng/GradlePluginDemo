package com.demo.myplugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.demo.myplugin.entity.DirectoryEntity
import com.demo.myplugin.entity.JarEntity
import com.demo.myplugin.entity.TransformEntity
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class ClassInjectTransform extends Transform {

    private Project mProject

    ClassInjectTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "DemoClassInject"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println('============================')
        println('DemoClassInject Start')
        println('============================')
        try {
            def android = mProject.extensions.findByType(AppExtension)
            ClassModifier.init(android)
            def lib = mProject.extensions.findByType(LibraryExtension)
            ClassModifier.initLib(lib)
            String projectName = mProject.rootProject.name
            TransformEntity entity = new TransformEntity()
            // 添加所有class文件path
            if (transformInvocation.inputs != null && !transformInvocation.inputs.isEmpty()) {
                for (TransformInput input : transformInvocation.inputs) {
                    if (input.directoryInputs != null && !input.directoryInputs.isEmpty()) {
                        for (DirectoryInput directoryInput : input.directoryInputs) {
                            // 获取output目录
                            def output = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                                    directoryInput.contentTypes, directoryInput.scopes,
                                    Format.DIRECTORY)
                            def changedFileMap = directoryInput.getChangedFiles()

                            DirectoryEntity directoryEntity = new DirectoryEntity(directoryInput.file, output, changedFileMap)
                            entity.directoryEntities.add(directoryEntity)
                            ClassModifier.appendClassPath(directoryInput.file.getAbsolutePath())
                        }
                    }
                    if (input.jarInputs != null && !input.jarInputs.isEmpty()) {
                        for (JarInput jarInput : input.jarInputs) {
                            //jar文件一般是第三方依赖库jar文件

                            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                            //生成输出路径
                            def output = transformInvocation.outputProvider.getContentLocation(jarInput.name,
                                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
                            // jar包解压后的保存路径
                            String jarZipDir = transformInvocation.getContext().getTemporaryDir().getAbsolutePath() + File.separator + jarInput.file.getName().replace('.jar', '') + md5Name

                            JarEntity jarEntity = new JarEntity(jarInput.file, output, jarZipDir, projectName, md5Name)
                            Util.unzipJar(jarEntity.inputFile.absolutePath, jarEntity.jarZipDir)
                            entity.jarEntities.add(jarEntity)
                            ClassModifier.appendClassPath(jarEntity.jarZipDir)
                        }
                    }
                }
            }

            ClassModifier.writeLog(entity)

            for (DirectoryEntity d : entity.directoryEntities) {
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(d.inputFile, d.outputFile)
                println("directory copy from ${d.inputFile} to ${d.outputFile}")
            }
            for (JarEntity j : entity.jarEntities) {
                def tempfile = null
//                if (j.hasChanged) {
                    String originalPath = j.outputFile.getAbsolutePath().replace('.jar', '.tmp')
                    // 从新打包jar
                    Util.zipJar(j.jarZipDir, originalPath)
                    println("reZip new jarFile from ${j.jarZipDir} to ${originalPath} ")
                    j.inputFile = new File(originalPath)
                    tempfile = j.inputFile
//                }

                //将输入内容复制到输出
                if (j.outputFile.exists() && (j.outputFile.lastModified() != j.inputFile.lastModified() || j.outputFile.length() != j.inputFile.length())) {
                    j.outputFile.delete()
                    println("output file exist and not same with input file , delete output file ${j.outputFile.absolutePath}")
                }
                if (!j.outputFile.exists()) {
                    FileUtils.copyFile(j.inputFile, j.outputFile)
                    println("copy jar file  from ${j.inputFile.absolutePath} to ${j.outputFile.absolutePath}")
                }

                if (null != tempfile) {
                    tempfile.delete()
                    println("delete temp file  ${tempfile.absolutePath} ")
                }

                if (j.removeCache) {
                    cacheFile.delete()
                    println("delete cache file  ${cacheFile.absolutePath} ")
                }
            }
        } catch (Throwable e) {
            e.printStackTrace()
        } finally {
            try {
                ClassModifier.workDoneClean()
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        println('============================')
        println('DemoClassInject Done')
        println('============================')
    }
}