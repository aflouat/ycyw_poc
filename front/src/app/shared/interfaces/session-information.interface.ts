
export interface SessionInformation {
  token: string;
  type: string;
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  admin: boolean;
  email: string; //TODO
  intercomJwt: string;
  intercomUserHash: string;
}
