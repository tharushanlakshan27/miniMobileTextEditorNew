package com.example.minimobileapplicationmad.editor

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*

class UndoRedoManager(private val editText: EditText) {

    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()
    private var isUndoOrRedo = false

    private val textWatcher = object : TextWatcher {
        private var beforeText: String = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (!isUndoOrRedo) {
                beforeText = s.toString()
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!isUndoOrRedo) {
                val currentText = s.toString()
                if (currentText != beforeText) {
                    undoStack.push(beforeText)
                    redoStack.clear()
                    // Limit stack size to manage memory
                    if (undoStack.size > 100) {
                        undoStack.removeAt(0)
                    }
                }
            }
        }
    }

    init {
        editText.addTextChangedListener(textWatcher)
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            isUndoOrRedo = true
            redoStack.push(editText.text.toString())
            editText.setText(undoStack.pop())
            editText.setSelection(editText.text.length)
            isUndoOrRedo = false
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            isUndoOrRedo = true
            undoStack.push(editText.text.toString())
            editText.setText(redoStack.pop())
            editText.setSelection(editText.text.length)
            isUndoOrRedo = false
        }
    }

    fun canUndo() = undoStack.isNotEmpty()
    fun canRedo() = redoStack.isNotEmpty()
    
    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }
}
