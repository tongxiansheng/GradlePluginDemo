package com.demo.myplugin.entity

class ClassEntity {
    String className
    String classPath

    ClassEntity(String name) {
        className = name
    }

    ClassEntity(String name, String path) {
        className = name
        classPath = path
    }
}