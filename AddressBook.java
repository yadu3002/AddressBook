import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

/**
 * An interactive contact address book - starter code
 * CSCU9P2 assignment Spring 2022
 *
 * THIS STARTER CODE WORKS PARTIALLY BUT SOME METHODS REQUIRE COMPLETING - SEE FURTHER DOWN IN THIS COMMENT
 *
 * The contact address book contains a 'database' of names, addresses and other details
 * - quite small in this version, but in principle it could be quite large.
 * At any one time the details of just one of the contacts will be on display.
 *
 * Buttons are provided to allow the user:
 *  o  to step forwards and backwards through the entries in the address book
 *  o  to add a new contact
 *  o  to delete the current or all contacts
 *  o  to search for a contact by exact name match
 *  o  to search for a contact by case insensitive match to any part of a name
 *  o  to re-order the contacts in ascending name order
 *  o  to re-order the contacts in descending name order
 *
 * Of course, in this exercise, the 'database' is a little unrealistic:
 * the information is built-in to the program (whereas in a 'serious' system it would,
 * perhaps, be read in from a file).
 *
 * *** TO BE DONE: ***
 * The navigation buttons function correctly.
 * The add new contact button is ALMOST CORRECT.
 * The core data processing methods for the other six buttons REQUIRE COMPLETING.
 *
 * You should insert your student number instead of 1234567 in line 117.
 *
 * All other work is on the METHOD BODIES BELOW line 445 (see /////////////////////////////):
 *
 * You must complete the addContact method, and implement full method bodies for
 * deleteContact, clearContacts, findContact, findPartial, sortAtoZ and sortZtoA.
 *
 * You MUST NOT alter any other parts of the program:
 *  o  the GUI parts are complete and correct
 *  o  the array declarations are complete and correct
 *  o  the method headers are complete and correct.
 * 
 * @author     
 * @version    2022
 */
public class AddressBook extends JFrame implements ActionListener
{
    /** Configuration: custom screen colours, layout constants and custom fonts. */
    private final Color
    veryLightGrey = new Color(240, 240, 240),
    darkBlue = new Color(0, 0, 150),
    backGroundColour = veryLightGrey,
    navigationBarColour = Color.lightGray,
    textColour = darkBlue;
    private static final int
    windowWidth = 450, windowHeight = 600,               // Overall frame dimensions
    windowLocationX = 200, windowLocationY = 100;        //     and position
    private final int
    panelWidth = 450, panelHeight = 250,                 // The drawing panel dimensions
    leftMargin = 50,                                     // All text and images start here
    mainHeadingY = 30,                                   // Main heading this far down the panel
    detailsY = mainHeadingY+40,                          // Details display starts this far down the panel
    detailsLineSep = 30;                                 // Separation of details text lines
    private final Font
    mainHeadingFont = new Font("SansSerif", Font.BOLD, 20),
    detailsFont = new Font("SansSerif", Font.PLAIN, 14);

    /** The navigation buttons. */
    private JButton
    first = new JButton("|<"),            // For "move to first contact" action
    previous = new JButton("<"),          // For "move to previous contact" action
    next = new JButton(">"),              // For "move to next contact" action
    last = new JButton(">|");             // For "move to final contact" action

    /** The action buttons */
    private JButton
    addContact = new JButton("Add new contact"),   // To request adding a new contact
    deleteContact = new JButton("Delete contact"), // To delete the currently selected contact
    deleteAll = new JButton("Delete all"),         // To delete all contacts
    findContact = new JButton("Find exact name"),  // To find contact by exact match of name
    findPartial = new JButton("Find partial name"),// To find contact by partial, case insensitive match of name
    sortAtoZ = new JButton("Sort A to Z"),         // To request re-ordering the contact by names A to Z
    sortZtoA = new JButton("Sort Z to A");         // To request re-ordering the contacts by name Z to A

    /** Text fields for data entry for adding new contact and finding a contact */
    private JTextField
    nameField = new JTextField(20),                // For entering a new name, or a name to find
    addressField = new JTextField(30),             // For entering a new address
    mobileField = new JTextField(12),              // For entering a new mobile number
    emailField = new JTextField(30);               // For entering a new email address

    /** The contact details drawing panel. */
    private JPanel contactDetails = new JPanel()
        {
            // paintComponent is called automatically when a screen refresh is needed
            public void paintComponent(Graphics g)
            {
                // g is a cleared panel area
                super.paintComponent(g); // Paint the panel's background
                paintScreen(g);          // Then the required graphics
            }
        };

    /**
     *  The main program launcher for the AddressBook class.
     *
     * @param  args  The command line arguments (ignored here).
     */
    public static void main(String[] args)
    {
        AddressBook contacts = new AddressBook();
        contacts.setSize(windowWidth, windowHeight);
        contacts.setLocation(windowLocationX, windowLocationY);
        contacts.setTitle("My address book: 3057915");
        contacts.setUpAddressBook();
        contacts.setUpGUI();
        contacts.setVisible(true);
    } // End of main

    /** Organizes overall set up of the address book data at launch time. */
    private void setUpAddressBook()
    {
        // Set up the contacts' details in the database
        currentSize = 0;    // No contacts initially
        addContact("John", "12 Cottrell Street, Stirling", "07999232321", "john@cs.isp.com");
        addContact("Paul", "23 Beatle Street, London", "0033998877", "paul@paul.net");
        addContact("George", "34 Beatle Street, New York", "01222 78160", "georgie@stirling.com");
        addContact("Simon", "45 Pathfoot Lane, Bridge of Allan", "0999 8888", "simon@simon.net");
        addContact("Leslie", "Box 3 , Glasgow", "3020 031221", "leslie@leslie.net"); 
        // currentSize should now be 5

        // Initially selected contact - the first in the database
        currentContact = 0;
    } // End of setUpAddressBook

    /** Sets up the graphical user interface.
     *
     * Some extra embedded JPanels are used to improve layout a little
     */
    private void setUpGUI()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());
        window.setBackground(navigationBarColour);

        // Set up the GUI buttons
        // The widget order is:
        // first (|<), previous (<), next (>), last (>|)

        window.add(new JLabel("Navigation:"));
        window.add(first);
        first.addActionListener(this);
        window.add(previous);
        previous.addActionListener(this);
        window.add(next);
        next.addActionListener(this);
        window.add(last);
        last.addActionListener(this);

        // Set up the details graphics panel
        contactDetails.setPreferredSize(new Dimension(panelWidth, panelHeight));
        contactDetails.setBackground(backGroundColour);
        window.add(contactDetails);

        // Set up action buttons
        JPanel addDelPanel = new JPanel();
        addDelPanel.add(addContact);
        addContact.addActionListener(this);
        addDelPanel.add(deleteContact);
        deleteContact.addActionListener(this);

        addDelPanel.add(deleteAll);
        deleteAll.addActionListener(this);
        window.add(addDelPanel);

        JPanel findPanel = new JPanel();
        findPanel.add(findContact);
        findContact.addActionListener(this);

        findPanel.add(findPartial);
        findPartial.addActionListener(this);
        window.add(findPanel);

        JPanel sortPanel = new JPanel();
        sortPanel.add(sortAtoZ);
        sortAtoZ.addActionListener(this);

        sortPanel.add(sortZtoA);
        sortZtoA.addActionListener(this);
        window.add(sortPanel);

        // Set up text fields for data entry
        // (using extra JPanels to improve layout control)
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("New/find name:"));
        namePanel.add(nameField);
        window.add(namePanel);

        JPanel addressPanel = new JPanel();
        addressPanel.add(new JLabel("New address:"));
        addressPanel.add(addressField);
        window.add(addressPanel);

        JPanel mobilePanel = new JPanel();
        mobilePanel.add(new JLabel("New mobile:"));
        mobilePanel.add(mobileField);
        window.add(mobilePanel);

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("New email:"));
        emailPanel.add(emailField);
        window.add(emailPanel);
    } // End of setUpGUI

    /**
     *  Display non-background colour areas, heading and currently selected database contact.
     *
     * @param  g  The Graphics area to be drawn on, already cleared.
     */
    private void paintScreen(Graphics g)
    {
        // Main heading
        g.setColor(textColour);
        g.setFont(mainHeadingFont);
        g.drawString("Contact details", leftMargin, mainHeadingY);

        // Current details
        displayCurrentDetails(g);
    } // End of paintScreen

    /**
     *  Display the currently selected contact.
     *
     * @param  g  The Graphics area to be drawn on.
     */
    private void displayCurrentDetails(Graphics g)
    {
        g.setColor(textColour);
        g.setFont(detailsFont);
        if (currentContact == -1)           // Check if no contact is selected, that is there are no contacts
            g.drawString("There are no contacts", leftMargin, detailsY);
        else
        {   // Display selected contact
            g.drawString(name[currentContact], leftMargin, detailsY);
            g.drawString(address[currentContact], leftMargin, detailsY + detailsLineSep);
            g.drawString("Mobile: " + mobile[currentContact], leftMargin, detailsY + 2 * detailsLineSep);
            g.drawString("Email: " + email[currentContact], leftMargin, detailsY + 3 * detailsLineSep);
        }
    } // End of displayCurrentDetails

    /**
     *  Handle the various button clicks.
     *
     * @param  e  Information about the button click
     */
    public void actionPerformed(ActionEvent e)
    {
        // If first is clicked: Cause the 0th contact to become selected (or -1 if there are none)
        if (e.getSource() == first)
            if (currentContact >= 0)
                currentContact = 0;
            else
                currentContact = -1;

        // If previous is clicked: Cause the previous contact to become selected, if there is one
        if (e.getSource() == previous && currentContact > 0)
            currentContact--;

        // If next is clicked: Cause the next contact to become selected, if there is one
        if (e.getSource() == next && currentContact < currentSize - 1)
            currentContact++;

        // If last is clicked: Cause the final available contact to become selected (or -1 if there are none)
        if (e.getSource() == last)
            currentContact = currentSize - 1;

        // Add a new contact
        if (e.getSource() == addContact)
            doAddContact();

        // Delete the current contact
        if (e.getSource() == deleteContact)
            doDeleteContact();

        // Delete all contacts
        if (e.getSource() == deleteAll)
            doDeleteAll();

        // Find a contact with exact name match
        if (e.getSource() == findContact)
            doFindContact();

        // Find a contact with partial, case insensitive name match
        if (e.getSource() == findPartial)
            doFindPartial();

        // Re-order the contacts by name A to Z
        if (e.getSource() == sortAtoZ)
            doSortAtoZ();

        // Re-order the contacts by name Z to A
        if (e.getSource() == sortZtoA)
            doSortZtoA();

        // And refresh the display
        repaint();
    } // End of actionPerformed

    /**
     * Add a new contact using data from the entry text fields
     *
     * Only adds if the name field is not empty (other fields do not matter),
     * and if there is space in the arrays.
     * Pops up dialogue box giving reason if contact is not added.
     * The new contact is selected immediately.
     */
    private void doAddContact()
    {
        String newName = nameField.getText();       nameField.setText("");
        String newAddress = addressField.getText(); addressField.setText("");
        String newMobile = mobileField.getText();   mobileField.setText("");
        String newEmail = emailField.getText();     emailField.setText("");
        if (newName.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No name entered");
            return;
        }
        if (newAddress.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No address entered");
            return;
        }
        if (newMobile.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No mobile number entered");
            return;
        }
        if (newEmail.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No email entered");
            return;
        }
        int index = addContact(newName, newAddress, newMobile, newEmail); // index is where added, or -1
        if (index == -1)                   // Check for success
            JOptionPane.showMessageDialog(null, "No space for new name");
        else
            currentContact = index;        // Immediately select the new contact
    } // End of doAddContact

    /**
     * Delete the currently selected contact
     *
     * If there are no contacts, then notify the user, but otherwise no action.
     * Otherwise delete, and the following remaining contact becomes selected.
     * If there is no following contact (that is, just deleted the highest indexed contact),
     * then the previous becomes selected.
     * If there is no previous (that is, just deleted contact 0), then all contacts have
     * been deleted and so no contact is selected.
     */
    private void doDeleteContact()
    {
        if (currentSize == 0)               // No contacts? If so do nothing
        {
            JOptionPane.showMessageDialog(null, "No contacts to delete");
            return;
        }
        deleteContact(currentContact);
        // currentContact is OK as the selected contact index, unless:
        if (currentContact == currentSize)    // Just deleted the highest indexed contact?
            currentContact--;                 // Adjust down to previous (or -1 if all deleted)
    } // End of doDeleteContact

    /**
     * Delete all the contacts - clear the list
     */
    private void doDeleteAll()
    {
        clearContacts();
        currentContact = -1;    // No contact selected
    } // End of doDeleteAll

    /**
     * Search for the contact whose name is an exact match to the name given in the name text field.
     *
     * The search name must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindContact()
    {
        String searchName = nameField.getText();
        if (searchName.length() == 0)               // Check and exit if the search name is empty
        {
            JOptionPane.showMessageDialog(null, "Name must not be empty");
            return;
        }
        int location = findContact(searchName);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else
        {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindContact

    /**
     * Search for the contact whose name contains the text given in the name text field,
     * case insensitively.
     *
     * The search text must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindPartial()
    {
        String searchText = nameField.getText();
        if (searchText.length() == 0)               // Check and exit if the search text is empty
        {
            JOptionPane.showMessageDialog(null, "Search text must not be empty");
            return;
        }
        int location = findPartial(searchText);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else
        {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindPartial

    /**
     * Re-order the contacts in the database so that the names are in ascending alphabetic order
     *
     * The first contact becomes selected, provided that there is one.
     */
    private void doSortAtoZ()
    {
        sortAtoZ();
        if (currentSize > 0)
            currentContact = 0;      // Index of the first contact
        else
            currentContact = -1;
    } // End of doSortAtoZ

    /**
     * Re-order the contacts in the database so that the names are in descending alphabetic order
     *
     * The first contact becomes selected, provided that there is one.
     */
    private void doSortZtoA()
    {
        sortZtoA();
        if (currentSize > 0)
            currentContact = 0;      // Index of the first contact
        else
            currentContact = -1;
    } // End of doSortZtoA

    //////////////////////////////////////////////////////////////////////////////////////////////

    /** Maximum capacity of the database. */
    private final int databaseSize = 10;

    /** To hold contacts' names, addresses, etc. */
    private String[]
    name = new String[databaseSize],
    address = new String[databaseSize],
    mobile = new String[databaseSize],
    email = new String[databaseSize];

    /** The current number of entries - always a value in range 0 .. databaseSize.
     *
     * The entries are held in elements 0 .. currentSize-1 of the arrays.
     */
    private int currentSize = 0;

    /** To hold index of currently selected contact
     *
     * There is always one selected contact, unless there are no entries at all in the database.
     * If there are one or more entries, then currentContact has a value in range 0 .. currentSize-1.
     * If there are no entries, then currentContact is -1.
     */
    private int currentContact = -1;

    /**
     * Add a new contact to the database in the next available location, if there is space.
     *
     * Return the index where added if successful, or -1 if no space so not added.
     */
    private int addContact(String newName, String newAddress, String newMobile, String newEmail)
    {
        if (currentSize != databaseSize)
        {
            name[currentSize] = newName;         // Add data at first free element in each array
            address[currentSize] = newAddress;
            mobile[currentSize] = newMobile;
            email[currentSize] = newEmail;
            currentSize++;                       // Count one more contact;
            return currentSize-1;                // Success, return where added
        }
        else
            return -1;                
    } // End of addContact

    /**
     * Delete the indicated contact from the database
     *
     * All contacts in subsequent (higher indexed) elements of the arrays are moved "down" to fill the "gap".
     * The order of the remaining contacts is unchanged (for example, if previously sorted alphabetically,
     * then so will they be after deletion).
     */
    private void deleteContact(int currentContact)
    {
        JOptionPane.showMessageDialog(null, name[currentContact]+" Deleted"); //  Message showing the contact deleted
        removeElement(currentContact,name);    //remove function for each address
        removeElement(currentContact,address);
        removeElement(currentContact,mobile);
        removeElement(currentContact,email);
        currentSize--;                         // reduce the contact list by 1
    } // End of deleteContact

    void removeElement(int removeIndex, String[] array)
    {
        for(int i = 0; i < currentSize; i++)
        {
            if(i == removeIndex)                      // Matching index with contact displayed
            {
                for (int j= removeIndex + 1; j < currentSize; j++)
                {
                    array[j - 1] = array[j];          // Shifting the elements by one to the left
                }
            }
        }
    } 

    /**
     * Clear the contacts database - set to empty
     */
    private void clearContacts()
    {
        currentSize=0;  // deleting contact
    } // End of clearContacts

    /**
     * Search the database for an exact match for the given name.
     *
     * Return the index of the match found, or -1 if no match found.
     */
    private int findContact(String searchName)
    {
        int search=-1;    //if it is not found, returns not found
        for(int i=0 ; i< currentSize;i++)
        {
            if (name[i].equals(searchName))
            {
                search = i;  
            }
        }
        return search;
    } // End of findContact

    /**
     * Search the database for a contact whose name contains the given search text, case insensitively.
     *
     * Return the index of the match found, or -1 if no match found.
     */
    private int findPartial(String searchText)
    {
        String Seek;
        int search=-1;
        String lowered= searchText.toLowerCase();   //Lowering the text to lowercase
        for(int i=0 ; i<currentSize;i++)
        {
            Seek=name[i].toLowerCase();
            if (Seek.contains(lowered))
            {
                search = i; 
            }
        }
        return search;                         // Return where found or -1
    } // End of findPartial

    /**
     * Re-order the contacts in the database so that the names are in ascending alphabetic order
     */
    private void sortAtoZ()
    {
        quickSort(name,0,currentSize-1);
    }

    private void quickSort(String[] a, int start, int end)
    {
        int i = start;     
        int j = end;
        if (j - i >= 1)
        {
            String pivot = a[i];   //choosing pivot
            while (j > i)
            {
                while (a[i].compareTo(pivot) <= 0 && i < end && j > i)
                {  //comparing pivot and strings in the list 
                    i++;
                }
                while (a[j].compareTo(pivot) >= 0 && j > start && j >= i)
                {
                    j--;
                }
                if (j > i)
                {
                    swap(a, i, j);     //swapping each list
                    swap(address,i,j);
                    swap(mobile,i,j);
                    swap(email,i,j);
                }
            }
            swap(a, start, j);
            swap(address,start,j);
            swap(mobile,start,j);
            swap(email,start,j);
            quickSort(a, start, j - 1);  //recursion
            quickSort(a, j + 1, end);
        }
    }

    /**
     * This method facilitates the quickSort method's need to swap two elements, Towers of Hanoi style.
     */
    private static void swap(String[] arr, int i, int j)
    {
        String tempName = arr[i];
        arr[i] = arr[j];
        arr[j] = tempName;
    }

    /**
     * Re-order the contacts in the database so that the names are in descending alphabetic order
     */
    private void sortZtoA()
    {
        quickSort(name,0,currentSize-1);                // Sorting the address book first
        reverse(name);                                  // Reversing all the details
        reverse(email);
        reverse(mobile);
        reverse(address);
    }
    private void reverse(String[] list)
    {
        String[] temp= new String[currentSize];         // Creating temporary list
        for(int i =0;i<currentSize;i++)
        {
            temp[i]=list[currentSize-1-i];              
        }
        for(int i =0;i<currentSize;i++)                 // Acquiring the sorted list from the temporary array  
        {
            list[i]=temp[i];
        }
    }
}
// End of AddressBook