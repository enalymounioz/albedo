package com.enalymounioz.albedo.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.enalymounioz.albedo.R
import com.enalymounioz.albedo.adapters.MemberListItemsAdapter
import com.enalymounioz.albedo.firebase.FirestoreClass
import com.enalymounioz.albedo.models.Board
import com.enalymounioz.albedo.models.User
import com.enalymounioz.albedo.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board
    // A global variable for Assigned Members List.
    private lateinit var mAssignedMembersList:ArrayList<User>

    private var anyChangesMade : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * A function to setup assigned members list into recyclerview.
     */
    fun setupMembersList(list: ArrayList<User>) {

        // TODO (Step 2: Initialize the Assigned Members List.)
        // START
        mAssignedMembersList = list
        // END

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this@MembersActivity)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        rv_members_list.adapter = adapter
    }


    /**
     * Method is used to show the Custom Dialog.
     */
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener(View.OnClickListener {

            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                // START
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
                // END
            } else {
                showErrorSnackBar("Please enter members email address.")
                /*Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        })
        dialog.tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        //Start the dialog and display it on screen.
        dialog.show()
    }
    // END

    // TODO (Step 3: Here we will get the result of the member if it found in the database.)
    // START
    fun memberDetails(user: User) {

        // TODO (Step 6: Here add the user id to the existing assigned members list of the board.)
        // START
        mBoardDetails.assignedTo.add(user.id)

        // TODO (Step 9: Finally assign the member to the board.)
        // START
        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
        // ENDss
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }

        super.onBackPressed()
    }

    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignSuccess(user: User) {

        hideProgressDialog()

        mAssignedMembersList.add(user)

        anyChangesMade = true

        setupMembersList(mAssignedMembersList)
    }
    // END
}