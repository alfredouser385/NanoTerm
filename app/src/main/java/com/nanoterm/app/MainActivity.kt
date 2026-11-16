
package com.nanoterm.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import java.io.File

class MainActivity : Activity() {

    private lateinit var terminal: TextView
    private lateinit var input: EditText
    private lateinit var scroll: ScrollView

    private var currentDir =
        File("/storage/emulated/0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        createTerminal()

        print("NanoTerm")
        print("Minimal Android Terminal")
        print("")
        print("$ pwd")
        print(currentDir.absolutePath)
        print("")
        prompt()
    }

    private fun createTerminal() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        scroll = ScrollView(this)

        terminal = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setPadding(12, 12, 12, 12)
        }

        scroll.addView(terminal)

        input = EditText(this).apply {
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setHint("Enter command...")
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setSingleLine(true)
            setPadding(12, 8, 12, 8)
            setBackgroundColor(Color.BLACK)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1, 0, 1f
            )
        )

        root.addView(
            input,
            LinearLayout.LayoutParams(-1, -2)
        )

        setContentView(root)

        input.setOnEditorActionListener {
            _, action, _ ->

            if (action == EditorInfo.IME_ACTION_DONE) {
                val command = input.text.toString()
                input.text.clear()
                execute(command)
                true
            } else {
                false
            }
        }
    }

    private fun execute(command: String) {

        if (command.isBlank()) {
            prompt()
            return
        }

        print("$ $command")

        when {

            command == "help" -> {
                print("Available commands:")
                print("help")
                print("pwd")
                print("ls")
                print("cd")
                print("clear")
                print("whoami")
            }

            command == "pwd" -> {
                print(currentDir.absolutePath)
            }

            command == "ls" -> {
                val files = currentDir.listFiles()

                if (files == null) {
                    print("Cannot read directory")
                } else {
                    files.sortedBy { it.name.lowercase() }
                        .forEach {
                            print(it.name +
                                if (it.isDirectory) "/" else "")
                        }
                }
            }

            command == "cd" -> {
                currentDir = File("/storage/emulated/0")
            }

            command.startsWith("cd ") -> {
                val path = command.substring(3).trim()

                val target = if (path.startsWith("/")) {
                    File(path)
                } else {
                    File(currentDir, path)
                }

                if (target.isDirectory) {
                    currentDir = target.canonicalFile
                } else {
                    print("Directory not found")
                }
            }

            command == "clear" -> {
                terminal.text = ""
            }

            command == "whoami" -> {
                print("nanoterm")
            }

            else -> {
                print("Command not found: $command")
                print("Type help for available commands.")
            }
        }

        print("")
        prompt()
    }

    private fun prompt() {
        print("$ ")
    }

    private fun print(text: String) {
        terminal.append(text + "\n")

        scroll.post {
            scroll.fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }
    }
}
