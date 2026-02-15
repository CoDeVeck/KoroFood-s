export const enviroment = {
  production: false,
  apigateway: 'http://localhost:8098',
  apiUrls: {
    usuarios: 'http://localhost:8081',
    menu: 'http://localhost:8087',
    eventos: 'http://localhost:8088',
    resenas: 'http://localhost:8098', // usando el apigateway
    pedido: 'http://localhost:8086',
    pago:'http://localhost:8085',
    reserva: 'http://localhost:8083',

    mesas: 'http://localhost:8082',
    usuarioSoap: 'http://localhost:8093'

  },
  githubClientId: 'Ov23liywDIDTbcViyzqf',
  githubRedirectUri: 'https://localhost:4200/auth/login',
};