package com.demo.myplugin.entity

import com.android.build.api.transform.Status

class DirectoryEntity extends BaseEntity {

    Map<File, Status> changeFiles

    DirectoryEntity(File inf, File outf, Map<File, Status> changed) {
        inputFile = inf
        outputFile = outf
        class2Process = new ArrayList<>()
        changeFiles = changed
    }
}