Create a Note taking Android application that has the following
features -
- Display a list of notes, persisted in local storage
- Add a note to the list of notes
- Delete a note
Each note item has the following properties
- Title
- Text
- A photo attachment(more on the photo attachment below)
- Created Time
The application will have three screens -

Screen #1: The launch screen displays the list of notes. Each list
item displays the Note title, and the Note text(truncated to 2 lines),
along with the creation time in a human-readable form(eg. 12th Jan
2016, 10:10 PM). The screen will also include an action(either an FAB
or a Toolbar action) to add a new Note. Triggering the action should
open screen 2. Tapping on a Note item should open screen 3.

Screen #2: The Add note screen should have fields to enter the Note
title and text(perform validation as necessary). An action to attach a
photo should be present on the screen. Photo attachment can be
retrieved using any source(either Camera or Web, upto the candidate)
and should be displayed in the note edit screen. The screen should
have a Save button that saves the Note locally and returns to Screen
1, where the newly added note is visible in the list of notes

Screen #3: The note detail screen should display the complete content
of the Note that was clicked, including the photo(if it is available).
Render the image using a custom ImageView that renders the image in
grayscale only when the user is touching the view and in normal colour
when the user is not touching the view. This screen should have an
Action that deletes the note and returns to the home screen. Deleted
note should no longer be visible. Allow editing of notes.
