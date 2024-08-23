import me.modmuss50.mpp.platforms.curseforge.CurseforgeDependencyContainer
import me.modmuss50.mpp.platforms.modrinth.ModrinthDependencyContainer

public data class DependencyContainer(val curse: CurseforgeDependencyContainer?, val modrinth: ModrinthDependencyContainer?) {
    public fun embeds(vararg slugs: String) {
        curse?.embeds(*slugs)
        modrinth?.embeds(*slugs)
    }

    public fun incompatible(vararg slugs: String) {
        curse?.incompatible(*slugs)
        modrinth?.incompatible(*slugs)
    }

    public fun optional(vararg slugs: String) {
        curse?.optional(*slugs)
        modrinth?.optional(*slugs)
    }

    public fun requires(vararg slugs: String) {
        curse?.requires(*slugs)
        modrinth?.requires(*slugs)
    }
}