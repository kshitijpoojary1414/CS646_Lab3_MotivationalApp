// define a package for the class
package com.kpoojary.quotesviewer

// import necessary Android libraries
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.abs

// define a constant for the motivation state
const val MOTIVATION_STATE = "motivationState"

// define a fragment for displaying motivational quotes
class MotivationFragment : Fragment() {
    // initialize private variables for tracking touch event and joke index
    private var initTouchY = 0
    private var jokeIndex = 0

    // initialize joke text view and quotes array
    private lateinit var jokeTextView: TextView
    private val quotesArray by lazy { resources.getStringArray(R.array.quotes) }

    // define a companion object for creating a new instance of the fragment
    companion object {
        fun newInstance() = MotivationFragment()
    }

    // create a context menu for the fragment
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.context_menu, menu)
    }

    // set up touch listener for the fragment view
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

    // restore the fragment's state and update the joke text view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            jokeIndex = savedInstanceState.getInt(MOTIVATION_STATE)
        }
        updateJoke()
    }

    // save the fragment's state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MOTIVATION_STATE, jokeIndex)
    }

    // handle context menu item selection
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

    // update the joke text view with the current quote
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
