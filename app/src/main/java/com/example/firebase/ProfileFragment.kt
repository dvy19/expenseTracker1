import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.firebase.CategoryActivity
import com.example.firebase.LoginActivity
import com.example.firebase.User
import com.example.firebase.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private var userBudget: Double = 0.0
    private var userName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebase()

        val category=binding.categoryWise.setOnClickListener{
            startActivity(Intent(requireContext(),CategoryActivity::class.java))
        }

        val logoutBtn = binding.logout

        logoutBtn.setOnClickListener {
            logoutUser()
        }

        loadUserData()
    }

    private fun logoutUser() {
        auth.signOut()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Show loading progress
            binding.progressBar.visibility = View.VISIBLE

            val userId = currentUser.uid
            val authEmail = currentUser.email ?: "No email" // Get email from Auth

            // ✅ UPDATED: Read from user's profile path "users/uid/profile"
            usersRef.child(userId).child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE

                    if (snapshot.exists()) {
                        // Get user data from database
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            updateUI(it, authEmail)
                        }
                    } else {
                        // If no data in database, show basic info
                        val user = User(
                            name = "Name not set",
                            gender = "-",
                            ageGroup = "-", // Updated from dob to ageGroup
                            profileImageUrl = ""
                        )
                        updateUI(user, authEmail)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    // Use auth data if database fails
                    val user = User(
                        name = "-",
                        gender = "-",
                        ageGroup = "-", // Updated from dob to ageGroup

                        profileImageUrl = ""
                    )
                    updateUI(user, authEmail)
                }
            })
        } else {
            showNoUserData()
        }
    }

    private fun updateUI(user: User, email: String) {
        binding.usernameShow.text = user.name
        binding.mailShow.text = email // Use email from Auth

    }

    private fun showNoUserData() {
        binding.usernameShow.text = "Please log in to view profile"
        binding.mailShow.text = ""
        binding.progressBar.visibility = View.GONE
    }
}