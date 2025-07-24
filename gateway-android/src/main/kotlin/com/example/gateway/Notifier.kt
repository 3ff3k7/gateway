package com.example.gateway

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.awt.image.BufferedImage

object Notifier {
    fun sendNotification(title: String, message: String) {
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()
            val image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            val trayIcon = TrayIcon(image, "Gateway")
            trayIcon.isImageAutoSize = true
            try {
                tray.add(trayIcon)
                trayIcon.displayMessage(title, message, MessageType.INFO)
                tray.remove(trayIcon)
            } catch (e: Exception) {
                println("[NOTIFY] $title - $message")
            }
        } else {
            println("[NOTIFY] $title - $message")
        }
    }
}
