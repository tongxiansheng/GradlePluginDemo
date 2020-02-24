package com.demo.myplugin.entity

class TransformEntity {
    List<DirectoryEntity> directoryEntities
    List<JarEntity> jarEntities
    List<ClassEntity> applicationEntities

    TransformEntity() {
        directoryEntities = new ArrayList<>()
        jarEntities = new ArrayList<>()
        applicationEntities = new ArrayList<>()
    }

}












