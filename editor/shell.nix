let
    nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/archive/9957cd48326fe8dbd52fdc50dd2502307f188b0d.tar.gz";
    pkgs = import nixpkgs { config.permittedInsecurePackages = [ "nodejs-18.17.1" ]; };
in
pkgs.mkShell {
    packages = with pkgs; [
        yarn
        python39
        nodejs_18
        pkg-config
        glib
        libsecret
    ];
}
