package com.example.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.note_design.view.*
import java.util.ArrayList

class noteAdapter(context:Context, noteList: ArrayList<Note>?):ArrayAdapter<Note>(context,0,noteList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view=LayoutInflater.from(context).inflate(R.layout.note_design,parent,false)
        var item=getItem(position)
        view.titletxt.text=item!!.title
        view.timetxt.text= item!!.time.toString()
        return view
    }
}