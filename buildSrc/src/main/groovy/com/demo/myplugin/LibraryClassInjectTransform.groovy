package com.demo.myplugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class LibraryClassInjectTransform extends ClassInjectTransform{

    LibraryClassInjectTransform(Project project) {
        super(project)
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return  TransformManager.PROJECT_ONLY
    }
}