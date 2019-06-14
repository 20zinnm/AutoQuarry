# AutoQuarry

> Automated quarries for the industrial Minecrafter.

## Installation

AutoQuarry requires [Vault](https://dev.bukkit.org/projects/vault) for economy and permissions integrations and [SmartInvs](https://github.com/MinusKube/SmartInvs/releases) for inventorty GUIs.

Additionally, a permissions plugin is recommended.

## Usage

To construct a quarry, place four iron bars in the sides of a 3x3 square. In the middle (touching all four bars), place a dispenser. The dispenser should be converted into a quarry.

Using a quarry is easy; just load fuel using a hopper and then press start in the menu.

## Permissions

* `autoquarry.create`: Required for a player to create a quarry.
* `autoquarry.place`: Required for a player to place a quarry.

## Roadmap

Pull requests that accomplish any of the following are likely to be merged:

- Permissions-based filters and upgrades.
- A more modular upgrade system (so it's not just a bunch of `if` statements).
- Make quarries place down scaffolding around the mining site. (Stretch goal.)
- Durability?

## License

GNU GPLv3. See the [LICENSE](LICENSE).