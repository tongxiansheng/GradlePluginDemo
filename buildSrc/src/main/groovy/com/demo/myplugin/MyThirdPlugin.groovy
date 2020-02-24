package com.demo.myplugin

import com.android.build.gradle.AppExtension
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyThirdPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println('hello from third plugin')

        def android = project.extensions.findByType(AppExtension)

        android.registerTransform(new ClassInjectTransform(project))

        android.applicationVariants.all { variant ->
            println("variant ${variant.flavorName} ${variant.buildType.name}")
            variant.outputs.each { output ->
                output.processManifest.outputs.upToDateWhen { false }
                output.processManifest.doLast {
                    ValueHolder.activities.clear()
                    ArrayList<File> manifestFileList = new ArrayList<>()

                    [output.processManifest.manifestOutputDirectory.asFile.get(),
                     output.processManifest.instantAppManifestOutputDirectory.asFile.get()
                    ].each { File directory ->
                        if (directory.exists()) {
                            File json = new File(directory, "output.json")
                            if (json.exists()) {
                                StringBuilder sb = new StringBuilder()
                                json.eachLine { String line ->
                                    sb.append(line)
                                }
                                println("Manifest output config json ${sb.toString()}")
                                com.demo.myplugin.entity.ManifestOutput[] manifestOutputEntities = new GsonBuilder().create()
                                        .fromJson(sb.toString(), new TypeToken<com.demo.myplugin.entity.ManifestOutput[]>() {
                                        }.getType())
                                if (null != manifestOutputEntities) {
                                    manifestOutputEntities.each { com.demo.myplugin.entity.ManifestOutput m ->
                                        File mFile = new File(directory, m.path)
                                        println("adding real manifest path ${mFile.getAbsolutePath()}")
                                        manifestFileList.add(mFile)
                                    }
                                }
                            }
                        }
                    }
                    manifestFileList.each { File manifestOutFile ->
                        if (manifestOutFile.exists()) {
                            println("adding manifestPath ${manifestOutFile.absolutePath}")
                            ManifestReader.processManifest(manifestOutFile.absolutePath, ValueHolder.activities)
                        }
                    }
                }
            }
        }
    }
}