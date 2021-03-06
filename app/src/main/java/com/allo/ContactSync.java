package com.allo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by baek_uncheon on 2015. 1. 27..
 */
public class ContactSync extends AsyncTask<String, String, String>{

    private static final int UPDATE_FRIENDS_LIST = 0;

    Context context;

    ContactSync(Context context) {
        this.context = context;
    }


    public void onFinishSyncContact(){

    }

    @Override
    protected void onPreExecute() {
        Log.i("Async", "onPreExecute");
    }


    @Override
    protected String doInBackground(String... params) {
        Log.i("Async", "onInBackground");
        syncLocalContacts();
        publishProgress("FINISH");
        return null;
    }

    @Override
    protected void onProgressUpdate(String... params) {
        Log.i("Async", "onProgressUpdate");
        if(params[0].equals("FINISH"))
            onFinishSyncContact();
    }

    @Override
    protected void onCancelled() {
        Log.i("Async", "onCancaelled");
    }



    public void syncLocalContacts() {
        ContactDBOpenHelper mContactDBOpenHelper = new ContactDBOpenHelper(context);
        mContactDBOpenHelper.open_writableDatabase();

        ArrayList<Contact> contact_list = getContactList();
        ArrayList<Contact> contact_db_list = getContactDBList();

        Log.i("before sync", String.valueOf(contact_db_list.size()) + "  : " + String.valueOf(contact_list.size()));

        String phone_number;
        Boolean is_new;

        for (int i = 0; i < contact_list.size(); i++) {
            phone_number = contact_list.get(i).getPhonenum();
            is_new = true;
            for (int j = 0; j < contact_db_list.size(); j++) {
                if (phone_number.equals(contact_db_list.get(j).getPhonenum())) {
                    is_new = false;
                }
            }
            if (is_new) {
                Log.i("tag", "is new ");
                mContactDBOpenHelper.setContact(phone_number, is_new);
            }
        }
        mContactDBOpenHelper.close();
    }


    public void syncFriendName(ArrayList<Friend> friend_array) {
        ArrayList<Contact> contact_array = getContactList();

        for (int i = 0; i < friend_array.size(); i++) {
            for (int j = 0; j < contact_array.size(); j++) {
                Friend friend = friend_array.get(i);
                Contact contact = contact_array.get(j);
                if (friend.getPhoneNumber().equals(contact.getPhonenum())) {
                    Log.i("sync friend name", contact.getNickname());
                    friend.setNickname(contact.getNickname());
                    friend_array.set(i, friend);
                    contact_array.remove(j);
                    break;
                }
            }
        }
    }


    public ArrayList<Contact> getContactDBList() {

        Cursor cursor;

        ContactDBOpenHelper mContactDBOpenHelper = new ContactDBOpenHelper(context);
        mContactDBOpenHelper.open_writableDatabase();


        cursor = mContactDBOpenHelper.getAllContacts();

        ArrayList<Contact> contact_db_list = new ArrayList<Contact>();

        if (cursor.moveToFirst()) {
            do {
                Contact aContact = new Contact();
                aContact.setPhonenum(cursor.getString(1));
                aContact.setNickname("");
                aContact.setIsNew(Boolean.parseBoolean(cursor.getString(2)));

                contact_db_list.add(aContact);

            } while (cursor.moveToNext());
        }

        return contact_db_list;

    }


    public ArrayList<Contact> getContactList() {


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = context.getContentResolver().query(uri, projection, null,
                selectionArgs, sortOrder);

        ArrayList<Contact> contact_list = new ArrayList<Contact>();

        if (contactCursor.moveToFirst()) {
            do {
                String phone_number = contactCursor.getString(1).replace("-", "");

                if (phone_number.startsWith("+82")) {
                    phone_number = phone_number.replace(
                            "+82", "0");
                }
                String nickname = contactCursor.getString(2);

                Contact aContact = new Contact();
                aContact.setPhonenum(phone_number);
                aContact.setNickname(contactCursor.getString(2));

                contact_list.add(aContact);
            } while (contactCursor.moveToNext());
        }
        return contact_list;
    }



}

