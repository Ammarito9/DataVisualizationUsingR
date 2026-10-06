library(lattice)

plotData <- function(values) {
    x <- 0:(length(values) - 1)

    png("/tmp/test-plot.png", width = 800, height = 500)
    print(
        xyplot(
            values ~ x,
            type = "l",
            col = "brown",
            xlim = c(0, 99),
            panel = function(x, y, ...) {
                panel.grid()
                panel.xyplot(x, y, type= "l", col = "brown")
            }
        )
    )

    dev.off()
}