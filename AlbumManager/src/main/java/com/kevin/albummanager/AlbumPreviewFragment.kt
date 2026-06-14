package com.kevin.albummanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.ui.AlbumPreviewPage

class AlbumPreviewFragment : Fragment() {
    companion object {
        private const val ARGS = "preview_args"

        fun newInstance(data: AlbumData): AlbumPreviewFragment {
            return AlbumPreviewFragment().apply {
                arguments = Bundle().apply { putParcelable(ARGS, data) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val data = arguments?.getParcelable<AlbumData>(ARGS)
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    data?.let {
                        AlbumPreviewPage(data = it)
                    }
                }
            }
        }
    }
}
