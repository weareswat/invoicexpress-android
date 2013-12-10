package pt.rupeal.invoicexpress.utils;

import android.annotation.SuppressLint;

import java.text.Normalizer;
import java.util.Comparator;

import pt.rupeal.invoicexpress.model.ContactModel;

@SuppressLint("DefaultLocale")
public class ContactsComparable implements Comparator<ContactModel>{

	@Override
	public int compare(ContactModel lhs, ContactModel rhs) {
		// normalizing and removing accents
		String leftContact = Normalizer.normalize(lhs.getName().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		String rightContact = Normalizer.normalize(rhs.getName().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		return leftContact.compareTo(rightContact);
	}

}
