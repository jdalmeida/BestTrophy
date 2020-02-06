

File holderFile = ScriptingEngine.fileFromGit(
	"https://github.com/jdalmeida/BestTrophy.git",
	"Shield_plate.stl");

CSG holder = Vitamins.get(holderFile)

return [holder]