package org.example

import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.net.URLClassLoader


class JarLoader {
    lateinit var engineClass:Class<*>
    lateinit var engineInstance:Any
    fun loadEngineJar(jarPath: String){
        val file = File(jarPath)

        val child = URLClassLoader(
            arrayOf<URL>(file.toURI().toURL()),
            this.javaClass.classLoader
        )
        engineClass = Class.forName("com.maherhanna.cheeta.core.Uci", true, child)
        engineInstance = engineClass.newInstance()


    }

    fun commandEngine(command:String):String{
        val method = engineClass.getDeclaredMethod("parseInput", String::class.java)
        return try {
            method.invoke(engineInstance,command) as String
        } catch (e: InvocationTargetException) {
            // should never get called.
            "error"
        }
    }
}