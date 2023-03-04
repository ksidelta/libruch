import { AuthorizedLayout } from "../components/Layout";
import {
  Avatar,
  Container,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
} from "@mui/material";

import DeleteIcon from "@mui/icons-material/Delete";

interface Book {
  id: string;
  title: string;
  author: string;
  thumbnailUrl: string;
  unavailableReason: string | null;
}

const Books = () => {
  const myBooks: Book[] = [
    {
      id: "id1",
      title: "Przeminęło z wiatrem",
      author: "Józef Piecyk",
      thumbnailUrl:
        "https://media.harrypotterfanzone.com/deathly-hallows-us-childrens-edition-1050x0-c-default.jpg",
      unavailableReason: null,
    },
    {
      id: "id2",
      title: "Harry Potter i komnata tajemnic",
      author: "Janina Rowling",
      thumbnailUrl:
        "https://media.harrypotterfanzone.com/deathly-hallows-us-childrens-edition-1050x0-c-default.jpg",
      unavailableReason: "borrowed",
    },
  ];

  const handleRemove = (id: string) => {
    console.log("Książkę usunę", id);
  };

  return (
    <AuthorizedLayout>
      <Container>
        <h1>Moje książki</h1>
        <List>
          {myBooks.map(
            ({ title, id, author, thumbnailUrl, unavailableReason }) => {
              const isDisabled = Boolean(unavailableReason);

              return (
                <ListItem
                  style={{
                    border: "1px solid",

                    borderRadius: 8,
                    marginBottom: 8,
                  }}
                  secondaryAction={
                    <IconButton
                      edge="end"
                      aria-label="delete"
                      disabled={isDisabled}
                      onClick={() => handleRemove(id)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  }
                  key={id}
                  className={`${isDisabled ? "Mui-disabled" : ""}`}
                >
                  <ListItemAvatar>
                    <Avatar src={thumbnailUrl} />
                  </ListItemAvatar>
                  <ListItemText
                    primary={title}
                    secondary={`${author} ${
                      isDisabled ? `(${unavailableReason})` : ""
                    }`}
                  />
                </ListItem>
              );
            }
          )}
        </List>
      </Container>
    </AuthorizedLayout>
  );
};

export default Books;
