# Projeto: Bem-Vindo

Este projeto consiste no desenvolvimento de uma aplicação Android utilizando Kotlin e Jetpack Compose, com integração ao Firebase para gerenciamento de autenticação de usuários. A aplicação permite que os usuários se registrem e façam login utilizando email e senha.  permite aos Usuários aprender uma nova lingua para o pais que emigram. Neste caso Português.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação principal.
- **Jetpack Compose**: Framework para construção de interfaces de usuário de forma declarativa.
- **Firebase Authentication**: Serviço para autenticação de usuários utilizando email e senha.
- **Firebase Database (Opcional)**: Para armazenamento de dados do usuário.
- **Android Studio**: IDE utilizada para o desenvolvimento da aplicação.

## Funcionalidades

1. **Página de Login**:
   - Usuários podem fazer login utilizando email e senha.
   - Validação de login com mensagens de erro personalizadas (ex: "Email ou mot de pass erronés!").
   - Redirecionamento para a página principal em caso de login bem-sucedido.

2. **Página de Registro**:
   - Usuários podem criar uma conta fornecendo informações como nome, número de segurança social, NIF, endereço, cidade, código postal, telefone, email e senha.
   - Validações de cada campo de entrada (ex: NIF, email, senha).
   - Mensagens de erro específicas para cada campo inválido.

3. **Validações de Formulário**:
   - Verificação de campo obrigatório para vários campos.
   - Validação de formato do email (verificação de `@` e `.`).
   - Senha deve ter pelo menos 8 caracteres, incluindo letras maiúsculas e minúsculas.
   - Validação do formato de NIF, Código Postal, e Número de Segurança Social.

4. **Interface Responsiva**:
   - Utilização do Jetpack Compose para criar interfaces de usuário modernas e reativas.
   
5. **Página de Confirmação de Presença**:
   - Validação da presença na aula por parte do aluno.
   - Visualização das aulas por vir e da sua semana.
   - Desmarcação das aulas e preferência de horários.
     
6. **Chat com o tutor**
   - Envio e Recepção de Messagens e Audios.
   - Possibilidade de avaliação em Live.

## Como Executar o Projeto

### Pré-requisitos

Antes de executar o projeto, você precisará das seguintes ferramentas instaladas:

- **Android Studio** (com suporte para Kotlin e Jetpack Compose).
- **Conta no Firebase** para configuração da autenticação.
- **Emulador ou dispositivo Android** para testar o aplicativo ou um dispositivo fisico.

### Passos para Configuração

1. **Clonar o repositório**:
   - Abra o terminal e clone o repositório:
     ```bash
     git clone https://github.com/Sen2pi/Bem-Vindo.git
     ```

2. **Configurar Firebase**:
   - Crie um novo projeto no [Firebase Console](https://console.firebase.google.com/).
   - Ative o serviço de autenticação por email e senha.
   - Baixe o arquivo de configuração `google-services.json` e adicione-o à pasta `app/` do seu projeto, clicando onde diz android e escolhendo projecto para podermos verificar os ficheiros e pastas.

3. **Instalar Dependências**:
   - Abra o Android Studio e abra o projeto clonado.
   - O Android Studio irá solicitar que você baixe as dependências necessárias automaticamente.

4. **Configurar as Dependências do Firebase**:
   - Adicione as dependências do Firebase no arquivo `build.gradle` do projeto (caso não estejam presentes):
     ```gradle
     implementation 'com.google.firebase:firebase-auth:21.0.1'
     implementation 'com.google.firebase:firebase-database:20.0.3'
     ```
   - Certifique-se de aplicar o plugin do Google Services no arquivo `build.gradle`:
     ```gradle
     apply plugin: 'com.google.gms.google-services'
     ```

5. **Rodar a aplicação**:
   - Após configurar o Firebase e as dependências, você pode rodar o aplicativo no emulador ou dispositivo Android.

## Estrutura do Projeto

O projeto segue a estrutura padrão para um aplicativo Android usando Jetpack Compose, com as seguintes pastas principais:

- **`src/main/java/pt/karimp/bem_vindo/`**: Contém as atividades, páginas e lógica do aplicativo.
  - **`paginas/`**: Contém as páginas da aplicação, como o login e registro.
  - **`auth/`**: Contém a lógica de autenticação com Firebase.
- **`res/`**: Contém os recursos do aplicativo, como imagens e layouts.

## Como Funciona o Código

1. **Tela de Login (`PaginaDeLogin`)**:
   - A página de login valida o email e a senha fornecidos e exibe mensagens de erro apropriadas.
   - Ao fazer login com sucesso, o usuário é redirecionado para a página principal (`home`).

2. **Tela de Registro (`PaginaDeRegistro`)**:
   - A página de registro permite que o usuário insira informações como nome, email, senha, etc.
   - Várias validações de campos são feitas antes de permitir o envio do formulário.
   - A conta do usuário é criada no Firebase Authentication após a validação do formulário.

3. **Lógica de Autenticação (`AuthenticationManager`)**:
   - A classe `AuthenticationManager` contém funções para login e registro com email e senha usando o Firebase Authentication.
   - Ela utiliza o Firebase para criar um novo usuário ou realizar login e retornar uma resposta de sucesso ou erro.
  
###Mockups : Isto é o MVP efectuado em figma do resultado que se pretende alcançar
![imagem 1 ](mockups/Captura de ecrã 2024-11-15 003446.png)

## Contribuições

Se você deseja contribuir com melhorias para este projeto, fique à vontade para criar um fork e submeter um pull request. Se encontrar algum bug ou tiver sugestões, abra uma issue no repositório.
