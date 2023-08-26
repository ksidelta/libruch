import { Login } from "@mui/icons-material";
import { useAuth } from "../features/auth/AuthProvider";
import Books from "./books";

export default function Home() {
  const { loading, user } = useAuth();

  if (loading) {
    return <>Ładowanie</>;
  }

  if (user) {
    return <Books />;
  }

  return <Login />;
}
