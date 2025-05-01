plugins {
    id("gg.essential.multi-version.root")
}

preprocess {
    val fabric12101 = createNode("1.21.1-fabric", 12101, "yarn")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")

    fabric12101.link(fabric12001)
}
