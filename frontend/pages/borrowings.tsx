import { AuthorizedLayout } from "../components/Layout";

interface availableBooks {
  "title": string;
  "isbn": string[];
  "description": string;
  "covers": string[],
  "subject_places": string[],
  "release_date": string[],
  "subjects": string[],
}

const Borrowings = () => {

  const availableBooks: availableBooks = {
    "title": "Harry Potter and the Philosopher's Stone",
    "isbn": [
      "9788700398368",
      "9788372780119",
      "1408865270"
    ],
    "description": "Harry Potter #1 When mysterious letters start arriving on his doorstep, Harry Potter has never heard of Hogwarts School of Witchcraft and Wizardry. They are swiftly confiscated by his aunt and uncle. Then, on Harry’s eleventh birthday, a strange man bursts in with some important news: Harry Potter is a wizard and has been awarded a place to study at Hogwarts. And so the first of the Harry Potter adventures is set to begin. ([source]) ----1",
    "covers": [
      "something.jpg",
      "something2.jpg"
    ],
		"subject_places": [
      "hogwart",
      "privet drive"
    ],
		"release_date": [
      "1998"
    ],
		"subjects": [
      "something",
      "something else"
    ],
  }

  return (
    <AuthorizedLayout>
      <div>


        lista książek do wypożyczenia, wypożyczone ksiązki, wypożycz książkę
        
      </div>
    </AuthorizedLayout>
  );
};

export default Borrowings;
