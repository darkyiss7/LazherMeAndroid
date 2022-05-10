package fr.isen.lazherme.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.isen.lazherme.R
import fr.isen.lazherme.databinding.FragmentCreateBinding

class JoinFragment : Fragment() {

    private lateinit var code: String
    private lateinit var userEmail: String
    private lateinit var userKey: String
    private lateinit var userName: String
    private lateinit var _binding : FragmentCreateBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join, container, false)
    }
}