package com.lxp.test

import scala.reflect.runtime.universe
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.{Credentials, UserGroupInformation}
import org.apache.hadoop.security.token.{TokenIdentifier, Token}


object HBaseTokenTest {
    def main(args: Array[String]): Unit = {
        val conf = new Configuration()
        obtainTokenForHBase(conf)
    }

    def obtainTokenForHBase(conf: Configuration): Unit = {
        if (UserGroupInformation.isSecurityEnabled) {
            val mirror = universe.runtimeMirror(getClass.getClassLoader)

            var credentials = UserGroupInformation.getCurrentUser.getCredentials

            //UserGroupInformation.getCurrentUser().addCredentials(credentials)

            try {
                val confCreate = mirror.classLoader.
                    loadClass("org.apache.hadoop.hbase.HBaseConfiguration").
                    getMethod("create", classOf[Configuration])
                val obtainToken = mirror.classLoader.
                    loadClass("org.apache.hadoop.hbase.security.token.TokenUtil").
                    getMethod("obtainToken", classOf[Configuration])

                //logDebug("Attempting to fetch HBase security token.")

                val hbaseConf = confCreate.invoke(null, conf).asInstanceOf[Configuration]
                if ("kerberos" == hbaseConf.get("hbase.security.authentication")) {
                    val token = obtainToken.invoke(null, hbaseConf).asInstanceOf[Token[TokenIdentifier]]
                    credentials.addToken(token.getService, token)
                    //logInfo("Added HBase security token to credentials.")
                }
            } catch {
                case e: java.lang.NoSuchMethodException =>
                    e.printStackTrace()
                    //logInfo("HBase Method not found: " + e)
                case e: java.lang.ClassNotFoundException =>
                    e.printStackTrace()
                    //logDebug("HBase Class not found: " + e)
                case e: java.lang.NoClassDefFoundError =>
                    e.printStackTrace()
                    //logDebug("HBase Class not found: " + e)
                case e: Exception =>
                    e.printStackTrace()
                    //logError("Exception when obtaining HBase security token: " + e)
            }
        }
    }
}
