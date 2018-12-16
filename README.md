# GDG-Hackfest

The app fethces JSON data and stores name of the note, its author, author institute name and all of its pages.
It uses SQLite database to store all these except for images. For images, we have stored it in app's private directory.

We reference all the fields using its materialID given to us by the JSON. Folders using these ids are created and respective images
are stored in it.
When the user wants to access these offline pages to access those saved notes.
