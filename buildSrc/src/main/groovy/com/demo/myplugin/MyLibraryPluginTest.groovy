package com.demo.myplugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyLibraryPluginTest implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println('hello from library plugin')
        def android = project.extensions.findByType(LibraryExtension)
        android.registerTransform(new LibraryClassInjectTransform(project))
    }
}