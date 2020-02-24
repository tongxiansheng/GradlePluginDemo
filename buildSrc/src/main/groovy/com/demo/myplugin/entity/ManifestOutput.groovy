package com.demo.myplugin.entity


class ManifestOutput {
    OutputType outputType
    APKInfo apkInfo
    String path
    Properties properties

    class OutputType {
        String type
    }

    class APKInfo {
        String type
        String[] splits
        int versionCode
    }

    class Properties {
        String packageId
        String split
        String minSdkVersion
    }
}