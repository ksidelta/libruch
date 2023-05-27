import { Box, Button, TextField } from "@mui/material";
import { AuthorizedLayout } from "../../components/Layout";
import Image from 'next/image';


export const Add = () => {

  const organisation = {
    name: 'Esports Lounge Gdańsk'
  };

  return (
    <AuthorizedLayout>
      <Box
        sx={{
          width: 500,
          maxWidth: '100%',
        }}
      >
        <p>
          Zostałeś zaproszony do organizacji <strong>{organisation.name}</strong> !!
        </p>
      </Box>
      <Image
        src="/welcome-greeting.gif"
        width={500}
        height={500}
        alt="Ziewający koteczek"
      />
      <Box
        sx={{
          width: 500,
          maxWidth: '100%',
        }}
      >
        <Button type="submit" fullWidth variant="contained">Dołącz</Button>
      </Box>
    </AuthorizedLayout>
  );
}

export default Add;
