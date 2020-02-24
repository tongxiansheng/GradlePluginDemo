package com.demo.myplugin


class ManifestReader {

    static void processManifest(String manifestPath, Set<String> activities) {
        def manifest = new XmlSlurper().parse(manifestPath)
        println("ManifestReader:  processManifest ${manifestPath}")
        manifest."application".each { p ->
            p."activity".each { a ->
                String name = a."@android:name"
                println("activity name: $name")
                if (null != name && name.length() > 0) {
                    activities.add(name)
                }
            }
        }
    }
}