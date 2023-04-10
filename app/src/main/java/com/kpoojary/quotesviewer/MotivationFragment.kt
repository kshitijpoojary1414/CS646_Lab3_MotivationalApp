package com.kpoojary.quotesviewer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.abs

const val MOTIVATION_STATE = "motivationState"

class MotivationFragment : Fragment() {
    private var initTouchY = 0
    private var jokeIndex = 0


    private lateinit var jokeTextView: TextView

    private val quotesArray by lazy { resources.getStringArray(R.array.quotes) }

    companion object {
        fun newInstance() = MotivationFragment()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.context_menu, menu)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val parentView = inflater.inflate(R.layout.fragment_joke, container, false)
        jokeTextView = parentView.findViewById(R.id.jokeTextView)
        registerForContextMenu(jokeTextView)

        parentView.setOnTouchListener { v, event ->
            var returnVal = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initTouchY = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val y = event.y.toInt()
                    if (abs(y - initTouchY) >= 300) {
                        jokeIndex += if (y > initTouchY) -1 else 1
                        updateJoke()
                        initTouchY = y
                    }
                }
                else -> returnVal = false
            }
            returnVal
        }

        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            jokeIndex = savedInstanceState.getInt(MOTIVATION_STATE)
        }
        updateJoke()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MOTIVATION_STATE, jokeIndex)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.next -> {
                jokeIndex++
                updateJoke()
                true
            }
            R.id.prev -> {
                jokeIndex--
                updateJoke()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun updateJoke() {
        if (jokeIndex < 0 || jokeIndex >= quotesArray.size) {
            AlertDialog.Builder(requireContext())
                .setTitle("No more quotes!")
                .setMessage("Click OK to continue.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }
        jokeTextView.text = quotesArray[jokeIndex]
    }

}
