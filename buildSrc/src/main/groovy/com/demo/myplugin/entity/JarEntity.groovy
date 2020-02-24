package com.demo.myplugin.entity

class JarEntity extends BaseEntity {
    String jarZipDir
    String projectName
    boolean hasChanged
    boolean saveCache
    boolean removeCache
    boolean useCache
    String pathMD5

    JarEntity(File inf, File outf, String jarZip, String project, pMD5) {
        inputFile = inf
        outputFile = outf
        jarZipDir = jarZip
        projectName = project
        class2Process = new ArrayList<>()
        hasChanged = false
        saveCache = false
        removeCache = false
        useCache = false
        pathMD5 = pMD5
    }
}