package com.sante.priceindex.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sante.priceindex.adapter.SlateAdapter
import com.sante.priceindex.databinding.FragmentDigitalSlateBinding
import com.sante.priceindex.viewmodel.PriceViewModel

class DigitalSlateFragment : Fragment() {

    private var _b: FragmentDigitalSlateBinding? = null
    private val b get() = _b!!
    private val vm: PriceViewModel by activityViewModels()
    private var isFullScreen = false

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDigitalSlateBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SlateAdapter { item -> vm.removeFromSlate(item.commodity) }
        b.rvSlate.adapter = adapter

        vm.slateItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            b.tvEmptySlate.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        b.btnFullScreen.setOnClickListener { toggleFullScreen() }
    }

    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        
        if (isFullScreen) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            b.btnFullScreen.text = "Exit Full Screen"
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            b.btnFullScreen.text = "📺 Full Screen"
        }
    }

    override fun onDestroyView() {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
        super.onDestroyView()
        _b = null
    }
}
