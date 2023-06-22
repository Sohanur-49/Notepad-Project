
package notepad;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class NotePad extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Trie dictionary;
    private String filePath;

    public NotePad() {
        super("Notepad");

        // Create components
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        JMenuBar menuBar = createMenuBar();

        // Initialize dictionary
        dictionary = new Trie();
        loadDictionary();

        // Set layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setJMenuBar(menuBar);

        // Set window properties
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem encryptItem = new JMenuItem("Encrypt");
        JMenuItem decryptItem = new JMenuItem("Decrypt");
        JMenuItem spellCheckItem = new JMenuItem("Spell Check");
        JMenuItem findReplaceItem = new JMenuItem("Find/Replace");

        editMenu.add(encryptItem);
        editMenu.add(decryptItem);
        editMenu.add(spellCheckItem);
        editMenu.add(findReplaceItem);

        encryptItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                encryptText();
            }
        });

        decryptItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decryptText();
            }
        });

        spellCheckItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spellCheckText();
            }
        });

        findReplaceItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findReplaceText();
            }
        });

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        return menuBar;
    }

    private void openFile() {
        fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                filePath = file.getAbsolutePath();
                Scanner scanner = new Scanner(file);
                StringBuilder text = new StringBuilder();
                
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine());
                    text.append("\n");
                }
                
                scanner.close();
                textArea.setText(text.toString());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFile() {
        fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                FileWriter writer = new FileWriter(file);
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void encryptText() {
        String plainText = textArea.getText();
        StringBuilder encryptedText = new StringBuilder();
        int shift = 3; // Caesar Cipher shift value
        
        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);
            
            if (Character.isLetter(c)) {
                c = (char) (c + shift);
                
                if (!Character.isLetter(c)) {
                    c = (char) (c - 26);
                }
            }
            
            encryptedText.append(c);
        }
        
        textArea.setText(encryptedText.toString());
    }

    private void decryptText() {
        String encryptedText = textArea.getText();
        StringBuilder decryptedText = new StringBuilder();
        int shift = 3; // Caesar Cipher shift value
        
        for (int i = 0; i < encryptedText.length(); i++) {
            char c = encryptedText.charAt(i);
            
            if (Character.isLetter(c)) {
                c = (char) (c - shift);
                
                if (!Character.isLetter(c)) {
                    c = (char) (c + 26);
                }
            }
            
            decryptedText.append(c);
        }
        
        textArea.setText(decryptedText.toString());
    }

    private void spellCheckText() {
        String[] words = textArea.getText().split("\\s+");

        for (String word : words) {
            if (!dictionary.search(word.toLowerCase())) {
                int choice = JOptionPane.showConfirmDialog(this, "Unknown word: " + word + "\nAdd to dictionary?", "Spell Check", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    dictionary.insert(word.toLowerCase());
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, "Spell check complete.");
    }

    private void findReplaceText() {
        String findText = JOptionPane.showInputDialog(this, "Enter text to find:");
        String replaceText = JOptionPane.showInputDialog(this, "Enter replacement text:");
        String text = textArea.getText();
        String newText = text.replaceAll(findText, replaceText);
        textArea.setText(newText);
    }

    private void loadDictionary() {
        try {
            File dictionaryFile = new File("D:/NotePad/src/notepad/dictionary.txt");
            Scanner scanner = new Scanner(dictionaryFile);
            
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                dictionary.insert(word.toLowerCase());
            }
            
            scanner.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NotePad();
            }
        });
    }
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            
            node = node.children[index];
        }
        
        node.isWord = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            
            if (node.children[index] == null) {
                return false;
            }
            
            node = node.children[index];
        }
        
        return node.isWord;
    }
}

class TrieNode {
    TrieNode[] children;
    boolean isWord;

    public TrieNode() {
        children = new TrieNode[26];
        isWord = false;
    }
}
//Al-hamdullah......
