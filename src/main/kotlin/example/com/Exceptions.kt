package example.com

class DuplicateUserException(email: String) : Exception("User with email $email already exists")